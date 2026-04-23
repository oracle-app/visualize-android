package com.oracle.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
import com.oracle.visualize.ui.theme.VisualizeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisualizeTheme {
                ShareAndPostScreen()
            }
        }
    }
}

