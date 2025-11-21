# Debug Logger Library 🐛

A modern and powerful debugging library for Android, inspired by Chucker but focused on custom logging.

## Latest Version

[![Version 1.0.0](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)]()
Check the [CHANGELOG](https://github.com/IvanGarza07/Debug-Logger-Library/blob/develop/CHANGELOG.md)

## ✨ Features

- 📝 **Persistent Logging** - All logs are saved to Room Database
- 🔔 **Smart Notifications** - Shows recent logs in a persistent notification
- 🎨 **Modern UI with Compose** - Interface built entirely with Jetpack Compose and Material 3
- 🔍 **Advanced Filters** - Search and filter logs by level, text, tag, and date
- 📱 **Edge-to-edge** - Compatible with Android 14+ and proper rotation handling
- 🌲 **Timber Support** - Automatic detection and integration with Timber
- 🏗️ **Clean Architecture** - Clean architecture with well-defined layers
- ⚙️ **100% Configurable** - Customize everything with a simple call
- 🚀 **Super Easy to Use** - One line of code to install

## 📋 Requirements

- Android API 24+ (Android 7.0)
- Kotlin 1.9+
- Jetpack Compose

## 🚀 Installation

### 1. Add the library to your project

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ivangarza07:debuglogger:1.0.0")
    
    // Timber (OPTIONAL - for better logging)
    implementation("com.jakewharton.timber:timber:5.0.1")
}
```

### 2. Install in your Application (JUST ONE LINE!)

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // That's it! Just one line
        DebugLogger.install(
            context = this,
            config = LoggerConfig(
                showNotification = BuildConfig.DEBUG,
                maxLogs = 10000
            )
        )
    }
}
```

### 3. Request notification permissions (Android 13+)

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
}
```

## 📖 Usage

### Logging is Super Simple - Static Methods

```kotlin
// Anywhere in your code - ViewModels, Activities, Fragments, etc.
class MainViewModel : ViewModel() {
    fun loadData() {
        DebugLogger.d("MainViewModel", "Loading data")
        
        try {
            val data = fetchData()
            DebugLogger.i("MainViewModel", "Data loaded successfully")
        } catch (e: Exception) {
            DebugLogger.e("MainViewModel", "Failed to load", e)
        }
    }
}

// In Activities
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugLogger.i("MainActivity", "Activity created")
    }
}

// In any class
object NetworkUtil {
    fun checkConnection() {
        DebugLogger.v("NetworkUtil", "Checking connection")
    }
}
```

### Log Levels

| Method | Level   | Color  | Usage                          |
|--------|---------|--------|--------------------------------|
| `v()`  | VERBOSE | Gray   | Detailed debugging information |
| `d()`  | DEBUG   | Blue   | Development information        |
| `i()`  | INFO    | Green  | General information            |
| `w()`  | WARN    | Orange | Warnings                       |
| `e()`  | ERROR   | Red    | Errors                         |
| `a()`  | WTF     | Purple | Critical errors                |

### Additional Operations

```kotlin
// Clear all logs
DebugLogger.clearAll()

// Open logs screen manually
startActivity(Intent(context, LogsActivity::class.java))
```

## ⚙️ Configuration

### Simple Configuration

```kotlin
// In Application.onCreate()
DebugLogger.install(
    context = this,
    config = LoggerConfig(
        showNotification = BuildConfig.DEBUG  // Only in debug
    )
)
```

### Complete Configuration

```kotlin
DebugLogger.install(
    context = this,
    config = LoggerConfig(
        showNotification = true,               // Show notification
        enablePersistence = true,              // Save to DB
        maxLogs = 15000                        // Print to Logcat
    )
)
```

### LoggerConfig Parameters

| Parameter                | Type     | Default          | Description                                    |
|--------------------------|----------|------------------|------------------------------------------------|
| `showNotification`       | Boolean  | `true`           | Shows persistent notification with recent logs |
| `enablePersistence`      | Boolean  | `true`           | Saves logs to Room Database                    |
| `maxLogs`                | Int      | `5000`           | Maximum number of logs to keep (0 = unlimited) |

### Recommended Configuration

#### 🔧 Development (Debug Build)
```kotlin
LoggerConfig(
    showNotification = true,
    enablePersistence = true,
    maxLogs = 10000
)
```

## 🎯 Features

### Persistent Notification

- Customizable title
- Last 5 (configurable) logs with timestamp
- Emojis per log level
- "Clear" button to clear all logs
- Tap to open full history

### Logs Activity

- Complete list of all logs
- Visual level indicator (color)
- Precise timestamp with milliseconds
- Search and advanced filters
- Tap on a log to see full details
- Delete individual logs

### Filters

- Text search in message or tag
- Filter by log levels
- Total log counter
- Clear filter button

### Details Screen

- Complete message
- Full stack trace
- Complete timestamp with date
- Delete individual log

## 🏗️ Architecture

```
debuglogger/
├── data/
│   ├── database/           # Room Database
│   ├── dates/              # Format Date Utils
│   ├── logger/             # Public API
│   ├── notification/       # Notification Manager
│   └── repository/         # Repository Implementation
│   └── worker/             # Worker Database
├── di/                     # Singleton Manager
├── domain/
│   ├── enums/              # Domain Enums
│   ├── model/              # Domain Models
│   └── repository/         # Repository Interface
│   └── states/             # Domain States
├── presentation/
│   ├── components/         # Compose Components
│   └── theme/              # Material 3 Theme
│   └── ui/                 # Main UI
```

## 🔧 Technologies

- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Modern design system
- **Room** - Local persistence
- **Coroutines & Flow** - Asynchronous programming
- **Lifecycle** - Lifecycle management
- **Singleton Pattern** - Dependency management

## 📱 Compatibility

- ✅ Android 7.0+ (API 24+)
- ✅ Android 13+ Notification permissions
- ✅ Android 14+ Edge-to-edge
- ✅ Android 15+ Support
- ✅ Screen rotation
- ✅ Dark/Light theme
- ✅ Dynamic colors (Android 12+)
- ✅ Tablets

## 💡 Tips

### Use Only in Debug

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation(project(":debuglogger"))
    // Not included in release
}

// Application.kt
if (BuildConfig.DEBUG) {
    DebugLogger.install(this, LoggerConfig(
        showNotification = true
    ))
}
```

### Timber Integration

If you have Timber installed, the library detects it automatically:

```kotlin
dependencies {
    implementation("com.jakewharton.timber:timber:5.0.1")
}

// The library detects Timber and uses it automatically
// No additional configuration needed
```

### Open Logs Manually

```kotlin
// From anywhere in your app
context.startActivity(Intent(context, LogsActivity::class.java))
```

## 🎉 Advantages over Other Solutions
 
✅ **One Line** - Just `DebugLogger.install()`  
✅ **Static Methods** - Use `DebugLogger.d()` anywhere  
✅ **Zero Boilerplate** - No ViewModelFactory, no modules  
✅ **Plug & Play** - Works immediately

## 🤝 Contributing

Contributions are welcome. Please:
1. Fork the project
2. Create a branch for your feature
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

Apache License - See LICENSE file for details

## 🙏 Acknowledgments

Inspired by [Chucker](https://github.com/ChuckerTeam/chucker) - An excellent HTTP inspection library.

## 📞 Support

Issues? Suggestions? Open an issue on GitHub.

---

**Made with ❤️ for the Android community - No unnecessary dependencies**