package com.brewkery.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.brewkery.app.ui.BrewkeryApp
import com.brewkery.app.ui.theme.BrewkeryTheme

class MainActivity : ComponentActivity() {
    private val keepSystemSplash = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { keepSystemSplash.value }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.parseColor("#3A2418"),
                darkScrim = Color.parseColor("#17110E"),
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.parseColor("#2B1B14"),
                darkScrim = Color.parseColor("#17110E"),
            ),
        )
        setContent {
            BrewkeryTheme {
                BrewkeryApp(
                    onComposeSplashVisible = { keepSystemSplash.value = false },
                )
            }
        }
    }
}
