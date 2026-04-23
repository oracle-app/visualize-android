package com.oracle.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oracle.visualize.presentation.screens.mainScreen.MainScreen
<<<<<<< feature/change_viz_management_to_jetpack_navigation
=======
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
>>>>>>> develop
import com.oracle.visualize.ui.theme.VisualizeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisualizeTheme {
                MainScreen()
            }
        }
    }
}

