package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = LightLeafGreen,
    onPrimary = Color.Black,
    primaryContainer = ForestGreen,
    onPrimaryContainer = MintContainer,
    secondary = HarvestAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5C2300),
    onSecondaryContainer = AmberContainer,
    tertiary = SkyBlue,
    background = Color(0xFF121812),
    surface = Color(0xFF1A221A),
    onBackground = Color(0xFFE8EFE8),
    onSurface = Color(0xFFE8EFE8),
    surfaceVariant = Color(0xFF263226),
    onSurfaceVariant = Color(0xFFB0C0B0),
    outline = Color(0xFF425542)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LeafGreen,
    onPrimary = Color.White,
    primaryContainer = PaleGreenBg,
    onPrimaryContainer = ForestGreen,
    secondary = HarvestAmber,
    onSecondary = Color.White,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = Color(0xFF5C2300),
    tertiary = SkyBlue,
    onTertiary = Color.White,
    tertiaryContainer = SkyBlueLight,
    onTertiaryContainer = Color(0xFF013654),
    background = CreamBackground,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFEFF5ED),
    onSurfaceVariant = TextMuted,
    outline = BorderSubtle
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted agriculture palette for consistent brand feel
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
