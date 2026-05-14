package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.shareScreen.components.SuggestedUserRow
import com.oracle.visualize.presentation.screens.shareScreen.components.UserAvatar
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components.RemoveTeammateDialog

@Composable
fun ShareWithTeammatesScreen(
    visualizationId: String = "",
    viewModel: ShareWithTeammatesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ShareWithTeammatesUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is ShareWithTeammatesUiState.Content -> {
            ShareWithTeammatesContent(
                state   = state,
                onEvent = { event ->
                    when (event) {
                        is ShareWithTeammatesUiEvent.BackPressed -> onNavigateBack()
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }

        is ShareWithTeammatesUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ShareWithTeammatesContent(
    state: ShareWithTeammatesUiState.Content,
    onEvent: (ShareWithTeammatesUiEvent) -> Unit
) {
    state.removeDialogForUser?.let { user ->
        RemoveTeammateDialog(
            user      = user,
            onDismiss = { onEvent(ShareWithTeammatesUiEvent.DismissRemoveDialog) },
            onConfirm = { onEvent(ShareWithTeammatesUiEvent.ConfirmRemoveUser(user)) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ShareWithTeammatesTopBar(
                onBackPressed = { onEvent(ShareWithTeammatesUiEvent.BackPressed) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                TeammateSearchBar(
                    query         = state.emailQuery,
                    onQueryChange = { onEvent(ShareWithTeammatesUiEvent.EmailQueryChanged(it)) },
                    onClear       = { onEvent(ShareWithTeammatesUiEvent.EmailQueryChanged("")) }
                )

                if (state.suggestedUsers.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        HorizontalDivider(
                            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                        state.suggestedUsers.forEach { user ->
                            SuggestedUserRow(user = user) {
                                onEvent(ShareWithTeammatesUiEvent.SelectSuggestion(user))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TeammateList(
                    users    = state.sharedUsers,
                    onRemove = { user -> onEvent(ShareWithTeammatesUiEvent.RequestRemoveUser(user)) }
                )

                if (state.sharedUsers.isEmpty() && state.emailQuery.isEmpty()) {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stringResource(R.string.share_with_teammates_empty),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (state.emailQuery.isNotEmpty() && state.suggestedUsers.isEmpty()) {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stringResource(R.string.share_no_results),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            TeammateShareBottomBar(
                isSubmitting   = state.isSubmitting,
                onConfirmShare = { onEvent(ShareWithTeammatesUiEvent.ConfirmShare) }
            )
        }
    }
}

@Composable
private fun ShareWithTeammatesTopBar(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier
                .fillMaxWidth()
                .requiredHeight(64.dp)
                .padding(start = 4.dp)
        ) {
            Box(
                modifier         = Modifier.requiredSize(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick  = onBackPressed,
                    modifier = Modifier.requiredSize(40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint               = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text       = stringResource(R.string.share_with_teammates_title),
                color      = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 1.29.em,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                modifier   = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun TeammateSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier         = Modifier.requiredSize(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (query.isEmpty()) {
                Icon(
                    imageVector        = Icons.Default.Search,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier           = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier           = Modifier.size(24.dp)
                )
            }
        }

        TextField(
            value         = query,
            onValueChange = onQueryChange,
            placeholder   = {
                Text(
                    text     = stringResource(R.string.input_email),
                    color    = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            },
            singleLine = true,
            colors     = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor             = MaterialTheme.colorScheme.primary,
                focusedTextColor        = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor      = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier  = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        if (query.isNotEmpty()) {
            IconButton(onClick = onClear, modifier = Modifier.requiredSize(48.dp)) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = stringResource(R.string.share_clear_search),
                    tint               = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier           = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredSize(48.dp))
        }
    }
}

@Composable
private fun TeammateList(
    users: List<ShareUser>,
    onRemove: (ShareUser) -> Unit
) {
    if (users.isEmpty()) return

    val visible = remember { mutableStateListOf(*users.toTypedArray()) }
    LaunchedEffect(users) {
        visible.clear()
        visible.addAll(users)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        visible.forEach { user ->
            AnimatedVisibility(
                visible = user in users,
                enter   = fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                TeammateRow(user = user, onRemove = { onRemove(user) })
            }
        }
    }
}

@Composable
private fun TeammateRow(
    user: ShareUser,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        UserAvatar(user = user, size = 44)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = user.username,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 1.5.em
            )
            Text(
                text       = user.email,
                fontSize   = 14.sp,
                color      = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 1.43.em
            )
        }

        IconButton(
            onClick  = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = stringResource(R.string.icon_remove_user),
                tint               = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TeammateShareBottomBar(
    isSubmitting: Boolean,
    onConfirmShare: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .requiredHeight(100.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Button(
            onClick  = onConfirmShare,
            enabled  = !isSubmitting,
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor   = Color.White
            ),
            modifier = Modifier
                .requiredWidth(80.dp)
                .requiredHeight(56.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color       = Color.White,
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector        = Icons.Outlined.Send,
                    contentDescription = stringResource(R.string.share_send),
                    modifier           = Modifier.size(24.dp)
                )
            }
        }
    }
}
