package com.oracle.visualize.presentation.screens.createEditScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.shareScreen.components.UserAvatar

/**
 * Entry-point composable for the Create/Edit Team screen.
 * Observes [CreateEditTeamViewModel] state and navigates back on success or discard.
 *
 * @param onNavigateBack Callback invoked when the operation completes or the user taps back.
 * @param viewModel The [CreateEditTeamViewModel] instance (injected by Hilt).
 */
@Composable
fun CreateEditTeamPage(
    onNavigateBack: () -> Unit,
    viewModel: CreateEditTeamViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateBack by viewModel.navigateBack.collectAsStateWithLifecycle()

    // Navigate back when the ViewModel signals it (submit success or confirmed discard)
    LaunchedEffect(uiState) {
        if (uiState is CreateEditTeamUiState.Success) onNavigateBack()
    }
    LaunchedEffect(navigateBack) {
        if (navigateBack) onNavigateBack()
    }

    when (val state = uiState) {
        is CreateEditTeamUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CreateEditTeamUiState.Content -> {
            CreateEditTeamContent(
                state   = state,
                onEvent = viewModel::onEvent
            )
            // Unsaved changes dialog — rendered on top of content
            if (state.showUnsavedChangesDialog) {
                UnsavedChangesDialog(
                    onConfirm = { viewModel.onEvent(CreateEditTeamUiEvent.ConfirmDiscard) },
                    onDismiss = { viewModel.onEvent(CreateEditTeamUiEvent.DismissUnsavedChangesDialog) }
                )
            }
        }
        is CreateEditTeamUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is CreateEditTeamUiState.Success -> {}
    }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

/**
 * Dialog shown when the user taps back with unsaved changes.
 * Matches the Figma design: neutral confirm button color (primary).
 */
@Composable
private fun UnsavedChangesDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = MaterialTheme.colorScheme.surface,
        shape            = RoundedCornerShape(28.dp),
        title = {
            Text(
                text       = stringResource(R.string.dialog_unsaved_changes_title),
                fontSize   = 24.sp,
                fontWeight = FontWeight.Normal,
                color      = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text  = stringResource(R.string.dialog_unsaved_changes_message),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text  = stringResource(R.string.dialog_unsaved_changes_cancel),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text  = stringResource(R.string.dialog_unsaved_changes_confirm),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    )
}

@Composable
private fun CreateEditTeamContent(
    state: CreateEditTeamUiState.Content,
    onEvent: (CreateEditTeamUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(top = 48.dp, bottom = 24.dp, start = 8.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onEvent(CreateEditTeamUiEvent.RequestBack) }) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.icon_back),
                            tint               = MaterialTheme.colorScheme.onSurface,
                            modifier           = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text       = if (state.isEditMode) stringResource(R.string.edit_team_title)
                        else stringResource(R.string.create_team_title),
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {

                // ── Team name field — CREATE mode only ───────────────────────
                if (!state.isEditMode) {
                    item {
                        TextField(
                            value         = state.teamName,
                            onValueChange = { onEvent(CreateEditTeamUiEvent.NameChanged(it)) },
                            placeholder   = {
                                Text(
                                    text     = stringResource(R.string.create_team_name_placeholder),
                                    fontSize = 16.sp,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier   = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            colors     = TextFieldDefaults.colors(
                                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                errorContainerColor     = MaterialTheme.colorScheme.errorContainer,
                                focusedIndicatorColor   = MaterialTheme.colorScheme.onSurface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                errorIndicatorColor     = MaterialTheme.colorScheme.error,
                                focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                            ),
                            textStyle  = LocalTextStyle.current.copy(fontSize = 16.sp),
                            isError    = state.nameError != null,
                            singleLine = true
                        )
                        if (state.nameError != null) {
                            Text(
                                text     = state.nameError,
                                color    = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (!state.isEditMode) {
                    item {
                        Text(
                            text       = stringResource(R.string.create_team_add_people_section),
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSurface,
                            modifier   = Modifier.padding(
                                start  = 16.dp, end = 16.dp,
                                top    = 16.dp,  bottom = 8.dp
                            )
                        )
                    }
                }

                // ── Search bar ───────────────────────────────────────────────
                item {
                    // In edit mode: leadingIcon lupa + trailingIcon X cuando hay texto
                    // In create mode: solo trailingIcon lupa
                    TextField(
                        value         = state.searchQuery,
                        onValueChange = { onEvent(CreateEditTeamUiEvent.SearchQueryChanged(it)) },
                        placeholder   = {
                            Text(
                                text  = stringResource(
                                    if (state.isEditMode) R.string.edit_team_search_placeholder
                                    else R.string.create_team_search_placeholder
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier     = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(32.dp)),
                        leadingIcon  = if (state.isEditMode) ({
                            Icon(
                                imageVector        = Icons.Default.Search,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }) else null,
                        trailingIcon = if (state.isEditMode && state.searchQuery.isNotBlank()) ({
                            IconButton(onClick = {
                                onEvent(CreateEditTeamUiEvent.SearchQueryChanged(""))
                            }) {
                                Icon(
                                    imageVector        = Icons.Default.Close,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }) else if (!state.isEditMode) ({
                            Icon(
                                imageVector        = Icons.Default.Search,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }) else null,
                        colors     = TextFieldDefaults.colors(
                            focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor  = Color.Transparent,
                            errorIndicatorColor     = Color.Transparent,
                            focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                        ),
                        singleLine = true
                    )
                }

                // ── Search results ───────────────────────────────────────────
                if (state.searchQuery.isNotBlank() && state.searchResults.isNotEmpty()) {
                    items(state.searchResults) { user ->
                        SearchResultRow(
                            user  = user,
                            onAdd = { onEvent(CreateEditTeamUiEvent.AddMember(user)) }
                        )
                    }
                }

                // ── Suggestions ("People in the same teams as you") ──────────
                // Only shown in create mode when suggestions are available
                if (!state.isEditMode && state.suggestions.isNotEmpty()) {
                    item {
                        Text(
                            text       = stringResource(R.string.create_team_suggestions_section),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSurface,
                            modifier   = Modifier.padding(
                                start  = 16.dp, end = 16.dp,
                                top    = 16.dp, bottom = 8.dp
                            )
                        )
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.suggestions) { user ->
                                SuggestionItem(
                                    user    = user,
                                    onClick = { onEvent(CreateEditTeamUiEvent.AddMember(user)) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    Text(
                        text       = stringResource(R.string.create_team_member_list_section),
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface,
                        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }

                // ── Member rows ──────────────────────────────────────────────
                items(state.members) { member ->
                    MemberRow(
                        user     = member,
                        isOwner  = member.id == state.ownerID,
                        onRemove = { onEvent(CreateEditTeamUiEvent.RemoveMember(member)) }
                    )
                }

                // ── Empty state hint — shown when only the owner is present ──
                if (state.members.size <= 1) {
                    item {
                        Text(
                            text     = stringResource(R.string.create_team_empty_hint),
                            fontSize = 14.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────────
        // Create mode: + icon  |  Edit mode: ✓ icon
        FloatingActionButton(
            onClick        = { onEvent(CreateEditTeamUiEvent.Submit) },
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
                .size(72.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor   = MaterialTheme.colorScheme.onSecondary,
            shape          = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector        = if (state.isEditMode) Icons.Default.Check else Icons.Default.Add,
                contentDescription = stringResource(R.string.create_team_save_description),
                modifier           = Modifier.size(36.dp)
            )
        }
    }
}

// ── Private composables ───────────────────────────────────────────────────────

@Composable
private fun SuggestionItem(user: ShareUser, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        UserAvatar(user = user, size = 64)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text       = user.username,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center,
            maxLines   = 2,
            lineHeight = 14.sp,
            color      = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SearchResultRow(user: ShareUser, onAdd: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = 40)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text       = user.username,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text     = user.email,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MemberRow(user: ShareUser, isOwner: Boolean, onRemove: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = 52)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = user.username,
                fontWeight = FontWeight.Normal,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text     = user.email,
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isOwner) {
            Text(
                text     = stringResource(R.string.teams_owner_label),
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        } else {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = stringResource(R.string.create_team_remove_member_description),
                    tint               = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
