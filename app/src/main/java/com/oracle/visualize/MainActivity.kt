package com.oracle.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.screens.MainScreen.MainScreen
import com.oracle.visualize.presentation.screens.MainScreen.ThemeViewModel
import com.oracle.visualize.ui.theme.VisualizeTheme

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()

            VisualizeTheme(darkTheme = isDarkMode) {
                MainScreen(
                    onToggleTheme = themeViewModel::toggleTheme,
                    isDarkMode    = isDarkMode
                )
            }
        }
    }
}