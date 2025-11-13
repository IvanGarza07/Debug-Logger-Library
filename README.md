# Debug Logger Library 🐛

Una librería moderna y poderosa para debugging en Android, inspirada en Chucker pero enfocada en logging personalizado.

## ✨ Características

- 📝 **Logging persistente** - Todos los logs se guardan en Room Database
- 🔔 **Notificaciones inteligentes** - Muestra los últimos 5 logs en una notificación persistente
- 🎨 **UI moderna con Compose** - Interfaz construida completamente con Jetpack Compose y Material 3
- 🔍 **Filtros avanzados** - Busca y filtra logs por nivel, texto, tag y fecha
- 📱 **Edge-to-edge** - Compatible con Android 14+ y manejo correcto de rotación
- 🌲 **Soporte para Timber** - Detección automática e integración con Timber
- 🏗️ **Clean Architecture** - Arquitectura limpia con capas bien definidas
- 💉 **Hilt** - Inyección de dependencias moderna
- ⚙️ **100% Configurable** - Personaliza todo sin crear módulos Hilt
- ⚡ **Performance** - Uso eficiente de Coroutines y Flow

## 📋 Requisitos

- Android API 24+ (Android 7.0)
- Kotlin 1.9+
- Jetpack Compose
- Hilt

## 🚀 Instalación

### 1. Agrega la librería a tu proyecto

En tu `settings.gradle.kts`:

```kotlin
include(":debuglogger")
```

En tu `build.gradle.kts` de la app:

```kotlin
dependencies {
    debugImplementation(project(":debuglogger"))
    // O si está publicada:
    // debugImplementation("com.yourcompany:debuglogger:1.0.0")
    
    // Hilt (REQUERIDO)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    
    // Timber (OPCIONAL - para mejor logging)
    implementation("com.jakewharton.timber:timber:5.0.1")
}
```

### 2. Configura en tu Application (¡MUY FÁCIL!)

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configuración simple - solo cambia lo que necesites
        DebugLoggerInitializer.configure(
            context = this,
            config = LoggerConfig(
                showNotification = BuildConfig.DEBUG,
                maxLogs = 10000,
                notificationTitle = "MyApp Debug"
            )
        )
    }
}
```

### 3. Solicita permisos de notificación (Android 13+)

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
}
```

## 📖 Uso

### Logging Básico

```kotlin
// Inyección con Hilt (Recomendado)
@HiltViewModel
class MyViewModel @Inject constructor(
    private val debugLogger: DebugLogger
) : ViewModel() {
    
    fun doSomething() {
        debugLogger.debug("MyViewModel", "Doing something")
        debugLogger.info("MyViewModel", "Info message")
        debugLogger.warn("MyViewModel", "Warning message")
        debugLogger.error("MyViewModel", "Error occurred", exception)
    }
}

// Métodos Estáticos (Sin inyección)
DebugLogger.d("TAG", "Debug message")
DebugLogger.i("TAG", "Info message")
DebugLogger.e("TAG", "Error message", exception)
```

### Niveles de Log

| Método | Nivel | Color | Uso |
|--------|-------|-------|-----|
| `v()` | VERBOSE | Gris | Información detallada de debugging |
| `d()` | DEBUG | Azul | Información de desarrollo |
| `i()` | INFO | Verde | Información general |
| `w()` | WARN | Naranja | Advertencias |
| `e()` | ERROR | Rojo | Errores |
| `wtf()` | WTF | Púrpura | Errores críticos |

## ⚙️ Configuración

### Configuración Simple

```kotlin
// En Application.onCreate()
DebugLoggerInitializer.configure(
    context = this,
    config = LoggerConfig(
        showNotification = BuildConfig.DEBUG,  // Solo en debug
        maxLogs = 10000                        // Límite de logs
    )
)
```

### Configuración Completa

```kotlin
DebugLoggerInitializer.configure(
    context = this,
    config = LoggerConfig(
        showNotification = true,               // Mostrar notificación
        enablePersistence = true,              // Guardar en DB
        maxLogs = 15000,                       // Máximo de logs
        notificationTitle = "MyApp Debug",    // Título personalizado
        notificationLogsToShow = 5,            // Logs en notificación (1-7)
        autoDeleteOldLogs = true,              // Auto-limpieza
        minLogLevel = LogLevel.VERBOSE,        // Nivel mínimo
        enableLogcat = true                    // Imprimir en Logcat
    )
)
```

### Parámetros de LoggerConfig

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `showNotification` | Boolean | `true` | Muestra notificación persistente con logs recientes |
| `enablePersistence` | Boolean | `true` | Guarda logs en Room Database |
| `maxLogs` | Int | `5000` | Número máximo de logs a mantener (0 = ilimitado) |
| `notificationTitle` | String | `"Debug Logger"` | Título personalizado de la notificación |
| `notificationLogsToShow` | Int | `5` | Cantidad de logs en notificación (1-7) |
| `autoDeleteOldLogs` | Boolean | `true` | Elimina logs antiguos automáticamente |
| `minLogLevel` | LogLevel | `VERBOSE` | Nivel mínimo para persistir logs |
| `enableLogcat` | Boolean | `true` | Habilita impresión en Logcat |

### Configuraciones por Escenario

#### 🔧 Desarrollo (Debug Build)
```kotlin
LoggerConfig(
    showNotification = true,
    enablePersistence = true,
    maxLogs = 10000,
    notificationTitle = "DEV Logs",
    minLogLevel = LogLevel.VERBOSE,
    enableLogcat = true
)
```

#### 🧪 Staging/QA
```kotlin
LoggerConfig(
    showNotification = true,
    enablePersistence = true,
    maxLogs = 5000,
    notificationTitle = "STAGING",
    minLogLevel = LogLevel.DEBUG,
    enableLogcat = true
)
```

#### 🚀 Producción (si decides usar logging)
```kotlin
LoggerConfig(
    showNotification = false,        // Sin notificación visible
    enablePersistence = true,        // Solo guardar errores
    maxLogs = 500,
    minLogLevel = LogLevel.ERROR,    // Solo errores
    enableLogcat = false             // Sin logcat por seguridad
)
```

### Configuración por Build Variant

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val config = when (BuildConfig.BUILD_TYPE) {
            "debug" -> LoggerConfig(
                showNotification = true,
                maxLogs = 10000,
                minLogLevel = LogLevel.VERBOSE
            )
            "release" -> LoggerConfig(
                showNotification = false,
                maxLogs = 500,
                minLogLevel = LogLevel.ERROR,
                enableLogcat = false
            )
            else -> LoggerConfig()
        }
        
        DebugLoggerInitializer.configure(this, config)
    }
}
```

## 🎯 Funcionalidades

### Notificación Persistente

La notificación muestra:
- Título personalizable del logger
- Últimos 5 (configurable) logs
- Timestamp y nivel de cada log
- Botón "Clear" para limpiar todos los logs
- Al tocar la notificación, abre el activity con el historial completo

### Activity de Logs

El activity incluye:
- Lista de todos los logs con scroll infinito
- Indicador visual de nivel (color)
- Timestamp preciso (milisegundos)
- Tag y mensaje
- Indicador de excepciones adjuntas
- Tap en un log para ver detalles completos

### Filtros

- **Búsqueda por texto**: Filtra por mensaje o tag
- **Filtro por nivel**: Selecciona qué niveles mostrar
- **Contador de logs**: Total de logs almacenados
- **Clear filter**: Limpia todos los filtros activos

### Log Detail Sheet

Al hacer tap en un log:
- Ver mensaje completo
- Ver stack trace completo si hay excepción
- Ver timestamp con fecha completa
- Eliminar log individual
- Copiar información (próximamente)

## 🏗️ Arquitectura

```
debuglogger/
├── data/
│   ├── local/
│   │   ├── LogDatabase.kt
│   │   └── LogDao.kt
│   ├── notification/
│   │   ├── LogNotificationManager.kt
│   │   └── LogNotificationReceiver.kt
│   └── repository/
│       └── LogRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   └── LogEntry.kt
│   └── repository/
│       └── LogRepository.kt
├── presentation/
│   ├── LogsActivity.kt
│   ├── LogsViewModel.kt
│   ├── components/
│   │   └── [Composables]
│   └── theme/
│       └── Theme.kt
├── di/
│   └── [Hilt Modules]
├── DebugLogger.kt
└── DebugLoggerInitializer.kt
```

### Capas

- **Data**: Room Database, NotificationManager, Repository Implementation
- **Domain**: Models, Repository Interfaces
- **Presentation**: Compose UI, ViewModels
- **DI**: Hilt Modules

## 🔧 Tecnologías

- **Jetpack Compose** - UI moderna y declarativa
- **Material 3** - Design system moderno
- **Room** - Persistencia local
- **Hilt** - Inyección de dependencias
- **Coroutines & Flow** - Programación asíncrona
- **Lifecycle** - Manejo del ciclo de vida

## 📱 Compatibilidad

- ✅ Android 7.0+ (API 24+)
- ✅ Android 13+ Notification permissions
- ✅ Android 14+ Edge-to-edge
- ✅ Android 15+ Support
- ✅ Rotación de pantalla
- ✅ Dark/Light theme
- ✅ Dynamic colors (Android 12+)
- ✅ Tablets

## 💡 Tips

### Usar solo en Debug

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation(project(":debuglogger"))
    // En release no se incluye
}

// Application.kt
if (BuildConfig.DEBUG) {
    DebugLoggerInitializer.configure(this, LoggerConfig(
        showNotification = true,
        maxLogs = 10000
    ))
}
```

### Integración con Timber

Si tienes Timber instalado, la librería lo detecta automáticamente y usa Timber para el logging en Logcat:

```kotlin
dependencies {
    implementation("com.jakewharton.timber:timber:5.0.1")
}

// La librería detecta Timber y lo usa automáticamente
// No necesitas configuración adicional
```

### Abrir Logs Manualmente

```kotlin
// Desde cualquier parte de tu app
context.startActivity(Intent(context, LogsActivity::class.java))
```

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## 📄 Licencia

MIT License - Ver LICENSE file para detalles

## 🙏 Agradecimientos

Inspirado en [Chucker](https://github.com/ChuckerTeam/chucker) - Una excelente librería de inspección HTTP.

## 📞 Soporte

¿Problemas? ¿Sugerencias? Abre un issue en GitHub.

---

**Hecho con ❤️ para la comunidad Android**