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
import com.oracle.visualize.presentation.navigation.NavRoutes
import com.oracle.visualize.presentation.screens.profileScreen.components.ChartThemePicker
import com.oracle.visualize.presentation.screens.profileScreen.components.ProfileHeader
import com.oracle.visualize.presentation.screens.profileScreen.components.SettingsCard
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

                // Background Image Setup
                Image(
                    painter = painterResource(id = R.drawable.profilebgtransparent),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    alignment = Alignment.TopCenter,
                    contentScale = ContentScale.FillBounds
                )

                // Page content starts here.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(98.dp))

                    var expanded by remember { mutableStateOf(false) }

                    Box {
                        ProfileHeader(
                            userName = state.username,
                            email = state.eMail,
                            profileImageUrl = state.image,
                            onEditClick = { expanded = true }
                        )

                        AppDropdownMenu(
                            expanded = expanded,
                            onDismiss = { expanded = false },
                            items = listOf(
                                stringResource(R.string.take_photo) to {

                                    // Creates a temporary cache file to store the captured image.

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
                                stringResource(R.string.choose_photo) to {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                stringResource(R.string.delete_photo) to { showDeletePhotoDialog = true }
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ChartThemePicker(
                        selectedPalette = state.chartTheme,
                        onPaletteChange = { profileViewModel.setChartTheme(it) }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    SettingsCard(title = stringResource(R.string.about_title)) {
                        Text(
                            text = "${stringResource(R.string.version_declaration)} $appVersion\n${stringResource(R.string.developer)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${stringResource(R.string.terms_of_service)}\n${stringResource(R.string.licenses)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(64.dp))

                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .height(64.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.log_out),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            is ProfileUiState.PfpUpload -> {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.preview),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { showUnsavedChangesDialog = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    AsyncImage(
                        model = state.pfp,
                        contentDescription = stringResource(R.string.profile_img_description),
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        error = painterResource(R.drawable.profile_placeholder),
                    )

                    OutlinedButton(
                        onClick = {
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
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .height(64.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.edit),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    OutlinedButton(
                        onClick = { showDeletePhotoDialog = true },
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .height(64.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = {
                            profileViewModel.updatePfp(state.pfp ?: "")
                            profileViewModel.setUiState()
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .height(64.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.save_changes),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

            }

            else -> {}
        }
    }
}

@Composable
fun BasicDialog(
    title: String,
    message: String,
    confirm: String,
    cancel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancel)
            }
        }
    )
}
