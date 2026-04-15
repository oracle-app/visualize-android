package com.oracle.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oracle.visualize.ui.screens.chartselection.ChartSelectionScreen
import com.oracle.visualize.ui.theme.OracleretoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OracleretoTheme {
                ChartSelectionScreen()
            }
        }
    }
}
