package com.oracle.visualize

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.oracle.visualize.presentation.screens.SnippingTool.SnippingToolView
import com.oracle.visualize.presentation.screens.mainScreen.MainScreen
import com.oracle.visualize.ui.theme.VisualizeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

/**
 * The main entry point activity for the Visualize application.
 * This activity sets up the application theme and initial navigation/screen.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Prevent screenshots and screen recording for security
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        
        enableEdgeToEdge()



        setContent {
            val testBitmap = BitmapFactory.decodeResource(resources, R.drawable.forsnipping_placeholder)
            var resultBitmap by remember { mutableStateOf<Bitmap?>(null) }
            VisualizeTheme {
                if (resultBitmap != null) {
                    Image(
                        bitmap = resultBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val testBitmap = BitmapFactory.decodeResource(resources, R.drawable.forsnipping_placeholder)
                    SnippingToolView(
                        bitmap = testBitmap,
                        onDone = { resultBitmap = it },
                        onCancel = { }
                    )
                }

            }
        }



        /** Currently Disabled for Snipping Tool debugging.
        setContent {
            VisualizeTheme {
                MainScreen()
            }
        }
        */
    }
}
