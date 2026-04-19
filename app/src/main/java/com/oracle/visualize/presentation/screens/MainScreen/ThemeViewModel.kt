package com.oracle.visualize.presentation.screens.MainScreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// SharedPreferences key
private const val PREFS_NAME  = "visualize_prefs"
private const val KEY_IS_DARK = "is_dark_mode"

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Initialize from persisted preference — defaults to false (light)
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_IS_DARK, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Toggle between light and dark, persisting the choice
    fun toggleTheme() {
        _isDarkMode.update { current ->
            val next = !current
            prefs.edit().putBoolean(KEY_IS_DARK, next).apply()
            next
        }
    }

    // Explicit setter — useful when following system setting
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.update {
            prefs.edit().putBoolean(KEY_IS_DARK, enabled).apply()
            enabled
        }
    }
}