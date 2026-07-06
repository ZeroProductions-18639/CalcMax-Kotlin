package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: String = "blue",
    amoledMode: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (accentColor.lowercase()) {
                "emerald" -> {
                    if (darkTheme) {
                        darkColorScheme(
                            primary = EmeraldPrimaryDark,
                            background = EmeraldBgDark,
                            surface = EmeraldBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = EmeraldPrimaryLight,
                            background = EmeraldBgLight,
                            surface = EmeraldBgLight
                        )
                    }
                }
                "purple" -> {
                    if (darkTheme) {
                        darkColorScheme(
                            primary = PurplePrimaryDark,
                            background = PurpleBgDark,
                            surface = PurpleBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = PurplePrimaryLight,
                            background = PurpleBgLight,
                            surface = PurpleBgLight
                        )
                    }
                }
                "orange" -> {
                    if (darkTheme) {
                        darkColorScheme(
                            primary = OrangePrimaryDark,
                            background = OrangeBgDark,
                            surface = OrangeBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = OrangePrimaryLight,
                            background = OrangeBgLight,
                            surface = OrangeBgLight
                        )
                    }
                }
                "red" -> {
                    if (darkTheme) {
                        darkColorScheme(
                            primary = RedPrimaryDark,
                            background = RedBgDark,
                            surface = RedBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = RedPrimaryLight,
                            background = RedBgLight,
                            surface = RedBgLight
                        )
                    }
                }
                "cyan" -> {
                    if (darkTheme) {
                        darkColorScheme(
                            primary = CyanPrimaryDark,
                            background = CyanBgDark,
                            surface = CyanBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = CyanPrimaryLight,
                            background = CyanBgLight,
                            surface = CyanBgLight
                        )
                    }
                }
                "pink" -> {
                    if (darkTheme) {
                        darkColorScheme(
                            primary = PinkPrimaryDark,
                            background = PinkBgDark,
                            surface = PinkBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = PinkPrimaryLight,
                            background = PinkBgLight,
                            surface = PinkBgLight
                        )
                    }
                }
                else -> { // Default "blue"
                    if (darkTheme) {
                        darkColorScheme(
                            primary = BluePrimaryDark,
                            background = BlueBgDark,
                            surface = BlueBgDark
                        )
                    } else {
                        lightColorScheme(
                            primary = BluePrimaryLight,
                            background = BlueBgLight,
                            surface = BlueBgLight
                        )
                    }
                }
            }
        }
    }

    // Apply AMOLED modifications if enabled and in dark mode
    val finalColorScheme = if (darkTheme && amoledMode) {
        baseColorScheme.copy(
            background = AmoledBackground,
            surface = AmoledSurface,
            surfaceVariant = AmoledSurfaceVariant,
            scrim = Color.Black
        )
    } else {
        baseColorScheme
    }

    MaterialTheme(
        colorScheme = finalColorScheme,
        typography = Typography,
        content = content
    )
}
