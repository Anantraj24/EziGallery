package com.ezi.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ezi.gallery.core.model.ThemeMode
import com.ezi.gallery.core.theme.EziGalleryTheme
import com.ezi.gallery.feature.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = (application as EziGalleryApp).preferences

        setContent {
            val themeMode by preferences.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)

            EziGalleryTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }
}
