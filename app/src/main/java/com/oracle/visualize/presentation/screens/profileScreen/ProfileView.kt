package com.oracle.visualize.presentation.screens.profileScreen

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AppDropdownMenu
import com.oracle.visualize.presentation.components.BasicDialog
import com.oracle.visualize.presentation.navigation.NavRoutes
import com.oracle.visualize.presentation.screens.profileScreen.components.ChartThemePicker
import com.oracle.visualize.presentation.screens.profileScreen.components.ProfileHeader
import com.oracle.visualize.presentation.screens.profileScreen.components.SettingsCard
import com.oracle.visualize.presentation.screens.profileScreen.views.EditPfp
import com.oracle.visualize.presentation.screens.profileScreen.views.EditProfile
import com.yalantis.ucrop.UCrop
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    // This is where the UI state is fetched.

    val context = LocalContext.current
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers

    var showDeletePhotoDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            profileViewModel.setPfpUploadUi()
            imageUri?.let { profileViewModel.setPfpCapturedValue(it.toString()) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            profileViewModel.setPfpUploadUi()
            profileViewModel.setPfpCapturedValue(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imageUri?.let { cameraLauncher.launch(it) }
        }
    }

    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = result.data?.let { UCrop.getOutput(it) }
            resultUri?.let { profileViewModel.setPfpCapturedValue(it.toString()) }
        }
    }


    //Dialogs

    if (showDeletePhotoDialog) {
        BasicDialog(
            title = stringResource(R.string.delete_photo),
            message = stringResource(R.string.delete_photo_message),
            confirm = stringResource(R.string.delete),
            cancel = stringResource(R.string.cancel),
            onConfirm = {
                profileViewModel.deleteProfilePicture()
                showDeletePhotoDialog = false
            },
            onDismiss = { showDeletePhotoDialog = false }
        )
    }

    if (showUnsavedChangesDialog) {
        BasicDialog(
            title = stringResource(R.string.dialog_unsaved_title),
            message = stringResource(R.string.dialog_unsaved_message),
            confirm = stringResource(R.string.dialog_leave),
            cancel = stringResource(R.string.cancel),
            onConfirm = {
                profileViewModel.setUiState()
                showUnsavedChangesDialog = false
            },
            onDismiss = { showUnsavedChangesDialog = false }
        )
    }

    if (showLogoutDialog) {
        BasicDialog(
            title = stringResource(R.string.log_out_title),
            message = "",
            confirm = stringResource(R.string.log_out),
            cancel = stringResource(R.string.cancel),
            onConfirm = {
                profileViewModel.logout()
                navController.navigate(NavRoutes.Login)
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // This is where the page fetches the current app version.

    val unknown = stringResource(R.string.error_unknown)
    val appVersion = remember {
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: unknown
    }

    // Page layout start

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (val state = uiState) {

            is ProfileUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Ready -> {
                EditProfile(
                    appversion = appVersion,
                    username = state.username,
                    email = state.eMail,
                    image = state.image,
                    chartTheme = state.chartTheme,
                    onTakePhoto = {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File.createTempFile("photo_", ".jpg", context.cacheDir)
                        )
                        imageUri = uri
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onChoosePhoto = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onDeletePhoto = { showDeletePhotoDialog = true },
                    onPaletteChange = { profileViewModel.setChartTheme(it) },
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
            is ProfileUiState.PfpUpload -> {
                EditPfp(
                    pfp = state.pfp,
                    onBack = { showUnsavedChangesDialog = true },
                    onEditClick = {
                        state.pfp?.let { pfp ->
                            val destUri = Uri.fromFile(File(context.cacheDir, "cropped_pfp.jpg"))
                            val options = UCrop.Options().apply {
                                setCircleDimmedLayer(true)
                                setShowCropGrid(false)
                                setShowCropFrame(false)
                            }
                            val cropIntent = UCrop.of(Uri.parse(pfp), destUri)
                                .withAspectRatio(1f, 1f)
                                .withMaxResultSize(512, 512)
                                .withOptions(options)
                                .getIntent(context)
                            uCropLauncher.launch(cropIntent)
                        }
                    },
                    onDeleteClick = { showDeletePhotoDialog = true },
                    onSaveChanges = {
                        profileViewModel.updatePfp(state.pfp ?: "")
                        profileViewModel.setUiState()
                    }
                )
            }

            else -> {}
        }
    }
}

