package com.example.movieapp.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.movieapp.model.auth.TokenResponse;
import com.example.movieapp.model.auth.LoginRequest;
import com.example.movieapp.model.auth.TokenRequest;
import com.example.movieapp.model.auth.SessionResponse;
import com.example.movieapp.model.auth.AccountResponse;

public interface TMDbAuthService {
    @GET("authentication/token/new")
    Call<TokenResponse> getRequestToken();
    
    @POST("authentication/token/validate_with_login")
    Call<TokenResponse> validateWithLogin(@Body LoginRequest request);
    
    @POST("authentication/session/new")
    Call<SessionResponse> createSession(@Body TokenRequest request);
    
    @GET("account")
    Call<AccountResponse> getAccount(@Query("session_id") String sessionId);
} 