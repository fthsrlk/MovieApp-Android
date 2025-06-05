package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.TMDbAuthService;
import com.example.movieapp.model.auth.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonGuestLogin;
    private CheckBox checkBoxRememberMe;
    private TextView textViewTmdbLink;
    private TMDbAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Önce oturum kontrolü yap
        if (isLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }
        
        setContentView(R.layout.activity_login);
        
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGuestLogin = findViewById(R.id.buttonGuestLogin);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        textViewTmdbLink = findViewById(R.id.textViewTmdbLink);
        
        authService = ApiClient.getClient().create(TMDbAuthService.class);
        
        // Kayıtlı kullanıcı adını yükle
        loadSavedUsername();
        
        buttonLogin.setOnClickListener(v -> login());
        buttonGuestLogin.setOnClickListener(v -> loginAsGuest());
        
        textViewTmdbLink.setOnClickListener(v -> {
            String url = "https://www.themoviedb.org/signup";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }
    
    private void loadSavedUsername() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String savedUsername = prefs.getString("saved_username", "");
        boolean rememberMe = prefs.getBoolean("remember_me", false);
        
        if (rememberMe && !savedUsername.isEmpty()) {
            editTextUsername.setText(savedUsername);
            checkBoxRememberMe.setChecked(true);
        }
    }
    
    private void saveUsername(String username) {
        if (checkBoxRememberMe.isChecked()) {
            SharedPreferences.Editor editor = getSharedPreferences("auth", MODE_PRIVATE).edit();
            editor.putString("saved_username", username);
            editor.putBoolean("remember_me", true);
            editor.apply();
        } else {
            // Kayıtlı bilgileri temizle
            SharedPreferences.Editor editor = getSharedPreferences("auth", MODE_PRIVATE).edit();
            editor.remove("saved_username");
            editor.remove("remember_me");
            editor.apply();
        }
    }
    
    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String sessionId = prefs.getString("session_id", "");
        int accountId = prefs.getInt("account_id", -1);
        return (!sessionId.isEmpty() && accountId != -1) || prefs.getBoolean("is_guest", false);
    }
    
    private void login() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        
        if (username.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), 
                "Lütfen tüm alanları doldurun", 
                Snackbar.LENGTH_SHORT).show();
            return;
        }
        
        // Kullanıcı adını kaydet
        saveUsername(username);
        
        // İlk olarak request token al
        authService.getRequestToken().enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String requestToken = response.body().getRequestToken();
                    validateLogin(username, password, requestToken);
                }
            }
            
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void validateLogin(String username, String password, String requestToken) {
        LoginRequest loginRequest = new LoginRequest(username, password, requestToken);
        
        authService.validateWithLogin(loginRequest).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    createSession(response.body().getRequestToken());
                }
            }
            
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void saveAccountInfo(String sessionId, int accountId) {
        SharedPreferences.Editor editor = getSharedPreferences("auth", MODE_PRIVATE).edit();
        editor.putString("session_id", sessionId);
        editor.putInt("account_id", accountId);
        editor.apply();
    }
    
    private void createSession(String validatedToken) {
        TokenRequest tokenRequest = new TokenRequest(validatedToken);
        
        authService.createSession(tokenRequest).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String sessionId = response.body().getSessionId();
                    
                    // Hesap bilgilerini al
                    authService.getAccount(sessionId).enqueue(new Callback<AccountResponse>() {
                        @Override
                        public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                saveAccountInfo(sessionId, response.body().getId());
                                startMainActivity();
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<AccountResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Hesap bilgileri alınamadı", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Oturum oluşturulamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void startMainActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void loginAsGuest() {
        // Misafir hesabı için özel bir ID oluştur
        int guestId = -999;
        String guestSession = "guest_session";
        
        // Misafir bilgilerini kaydet
        SharedPreferences.Editor editor = getSharedPreferences("auth", MODE_PRIVATE).edit();
        editor.putString("session_id", guestSession);
        editor.putInt("account_id", guestId);
        editor.putBoolean("is_guest", true);
        editor.apply();
        
        startMainActivity();
        finish();
    }
} 