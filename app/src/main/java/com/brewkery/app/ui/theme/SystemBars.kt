package com.brewkery.app.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun BrewkerySystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    val statusBarColor = if (darkTheme) DarkBg else DarkCoffee
    val navigationBarColor = if (darkTheme) DarkBg else Espresso

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
    }
}
