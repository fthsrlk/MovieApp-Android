# 🎬 MovieApp Android

Modern ve kullanıcı dostu Android film ve dizi uygulaması. TMDb API entegrasyonu ile zengin içerik ve gelişmiş arama özellikleri sunar.

## ✨ Özellikler

### 🔍 Film ve Dizi Arama
- **Gelişmiş Arama**: Başlık, tür, yıl ve oyuncu bazlı arama
- **Akıllı Filtreler**: Popülerlik, puan ve çıkış tarihine göre sıralama
- **Kategori Browsing**: Popüler, en çok oy alan, vizyondaki filmler

### 📱 Modern UI/UX
- **Material Design**: Google'ın tasarım prensiplerine uygun arayüz
- **Responsive Layout**: Farklı ekran boyutlarına uyumlu tasarım
- **Dark/Light Theme**: Sistem temasına uyumlu renk paleti
- **Smooth Animations**: Akıcı geçişler ve animasyonlar

### 💾 Yerel Veri Yönetimi
- **SQLite Database**: Hızlı ve güvenilir veri saklama
- **Offline Support**: İnternet bağlantısı olmadan favorilere erişim
- **Cache System**: Görüntülenen içeriklerin yerel olarak saklanması

### ⭐ Kişisel Koleksiyon
- **Favori Listesi**: Beğenilen film ve dizileri kaydetme
- **İzleme Listesi**: İleride izlenecek içerikler için planlama
- **Puanlama Sistemi**: Kişisel puanlarla içerik değerlendirme
- **İzleme Durumu**: İzlenen, izleniyor, plan listesi

## 🏗️ Teknik Mimari

### 🔧 Teknolojiler
- **Java**: Ana programlama dili
- **Android SDK**: API Level 21+ (Android 5.0+)
- **SQLite**: Yerel veritabanı
- **Retrofit**: HTTP API istekleri
- **Gson**: JSON veri işleme
- **Glide**: Görüntü yükleme ve cache
- **RecyclerView**: Performanslı liste görüntüleme

### 🌐 API Entegrasyonu
- **TMDb API v3**: Film ve dizi verileri
- **RESTful Architecture**: Modern API tasarım prensipleri
- **Error Handling**: Robust hata yönetimi
- **Rate Limiting**: API kullanım limitlerini yönetme

### 📦 Proje Yapısı
```
app/src/main/java/com/example/movieapp/
├── activities/          # Ana aktiviteler
├── adapters/           # RecyclerView adapterleri
├── api/               # API client ve servisler
├── database/          # SQLite veritabanı
├── models/            # Veri modelleri
├── utils/             # Yardımcı sınıflar
└── fragments/         # UI fragment'ları
```

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Android Studio 4.2+
- Java 8+
- Android SDK 21+
- TMDb API Key

### Kurulum Adımları

1. **Repository'yi klonlayın:**
```bash
git clone https://github.com/fthsrlk/MovieApp-Android.git
cd MovieApp-Android
```

2. **Android Studio'da açın:**
- Android Studio'yu başlatın
- "Open an existing project" seçin
- Klonlanan klasörü seçin

3. **API Key yapılandırması:**
- `app/src/main/res/values/strings.xml` dosyasını açın
- TMDb API key'inizi ekleyin:
```xml
<string name="tmdb_api_key">YOUR_API_KEY_HERE</string>
```

4. **Projeyi derleyin:**
- `Build > Make Project` veya `Ctrl+F9`

5. **Uygulamayı çalıştırın:**
- Emulator veya fiziksel cihaz bağlayın
- `Run > Run 'app'` veya `Shift+F10`

## 📸 Ekran Görüntüleri

### Ana Menü
- Modern tasarımlı ana sayfa
- Hızlı erişim kategorileri
- Arama çubuğu

### Film Detay
- Yüksek çözünürlüklü poster
- Detaylı film bilgileri
- Oyuncu kadrosu
- Benzer film önerileri

### Favoriler
- Kişisel koleksiyon görünümü
- Hızlı filtreleme seçenekleri
- Düzenleme ve silme işlemleri

## 🔐 Güvenlik ve Gizlilik

- **API Key Security**: Güvenli API key saklama
- **User Data**: Kullanıcı verilerinin yerel olarak saklanması
- **Permission Management**: Minimal izin talep etme
- **Data Encryption**: Hassas verilerin şifrelenmesi

## 📈 Performans Optimizasyonları

- **Image Caching**: Glide ile akıllı görüntü cache
- **Database Indexing**: Hızlı veri erişimi için indexleme
- **Memory Management**: Efficient memory kullanımı
- **Background Processing**: Asenkron veri işleme

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakın.

## 📞 İletişim

**Fatih Şarlak**
- GitHub: [@fthsrlk](https://github.com/fthsrlk)
- Email: [fatihhars70@gmail.com]
- Linkedin: [https://www.linkedin.com/in/fatih-%C5%9Farlak-63b369275/]

## 🙏 Teşekkürler

- [TMDb](https://www.themoviedb.org/) - Film ve dizi verileri için
- [Material Design](https://material.io/) - Tasarım sistemi için
- [Android Community](https://developer.android.com/) - Dokümantasyon ve örnekler için

---

⭐ **Bu projeyi beğendiyseniz star vermeyi unutmayın!**
