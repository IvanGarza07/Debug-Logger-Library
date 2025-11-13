package com.igarza.debuglogger.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF003258),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF004A77),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD1E4FF),
    secondary = androidx.compose.ui.graphics.Color(0xFFBCC7DC),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF263141),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF3D4758),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFD8E3F8),
    tertiary = androidx.compose.ui.graphics.Color(0xFFD8BFD8),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF3B2948),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF533F5F),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFF5DBF4),
    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF93000A),
    onError = androidx.compose.ui.graphics.Color(0xFF690005),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    background = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    surface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF42474E),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC2C7CF),
    outline = androidx.compose.ui.graphics.Color(0xFF8C9199),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    inversePrimary = androidx.compose.ui.graphics.Color(0xFF00639B),
    surfaceTint = androidx.compose.ui.graphics.Color(0xFF90CAF9),
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF00639B),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD1E4FF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF001D33),
    secondary = androidx.compose.ui.graphics.Color(0xFF535E71),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFD7E3F8),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF101C2B),
    tertiary = androidx.compose.ui.graphics.Color(0xFF6B5778),
    onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFF3DBFF),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF251432),
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410002),
    background = androidx.compose.ui.graphics.Color(0xFFFCFCFF),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surface = androidx.compose.ui.graphics.Color(0xFFFCFCFF),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFDFE2EB),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF42474E),
    outline = androidx.compose.ui.graphics.Color(0xFF73777F),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFFF1F0F4),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFF2F3033),
    inversePrimary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    surfaceTint = androidx.compose.ui.graphics.Color(0xFF00639B),
)

@Composable
fun DebugLoggerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}