package com.oracle.visualize.presentation.screens.SnippingTool

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun SnippingToolView(
    imageUri: Uri,
    onDone: (Uri) -> Unit
) {
    val context = LocalContext.current

    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultUri = UCrop.getOutput(result.data!!)
        resultUri?.let { onDone(it) }
    }

    LaunchedEffect(imageUri) {
        val destinationUri = Uri.fromFile(
            File.createTempFile("cropped_", ".jpg", context.cacheDir)
        )
        val uCropIntent = UCrop.of(imageUri, destinationUri)
            .withOptions(UCrop.Options().apply {
                setHideBottomControls(false)
                setFreeStyleCropEnabled(true)
                setShowCropGrid(true)
                setShowCropFrame(true)
            })
            .getIntent(context)
        uCropLauncher.launch(uCropIntent)
    }
}
