package com.brewkery.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    onPrimary = Cream,
    primaryContainer = Latte,
    onPrimaryContainer = Espresso,
    secondary = Caramel,
    onSecondary = Espresso,
    secondaryContainer = Cappuccino,
    onSecondaryContainer = DarkCoffee,
    tertiary = DarkCoffee,
    onTertiary = Cream,
    background = Cream,
    onBackground = Espresso,
    surface = BrewWhite,
    onSurface = Espresso,
    surfaceVariant = Latte,
    onSurfaceVariant = MutedText,
    outline = Cappuccino,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Espresso,
    primaryContainer = DarkCoffee,
    onPrimaryContainer = DarkText,
    secondary = Caramel,
    onSecondary = Espresso,
    secondaryContainer = CoffeeBrown,
    onSecondaryContainer = DarkText,
    tertiary = Cappuccino,
    onTertiary = Espresso,
    background = DarkBg,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkCoffee,
    onSurfaceVariant = Latte,
    outline = CoffeeBrown,
)

@Composable
fun BrewkeryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val density = LocalDensity.current
    val compactFontScale = density.fontScale.coerceAtMost(1f) * 0.92f
    val balancedDensity = Density(
        density = density.density,
        fontScale = compactFontScale,
    )

    CompositionLocalProvider(LocalDensity provides balancedDensity) {
        BrewkerySystemBars(darkTheme = darkTheme)
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BrewkeryTypography,
            content = content,
        )
    }
}
