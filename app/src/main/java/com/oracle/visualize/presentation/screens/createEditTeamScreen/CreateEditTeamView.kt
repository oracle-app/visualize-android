package com.oracle.visualize.presentation.screens.createEditTeamScreen

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
import com.oracle.visualize.presentation.components.UserAvatar
import com.oracle.visualize.ui.theme.primary


@Composable
fun CreateEditTeamPage(
    onNavigateBack: () -> Unit,
    viewModel: CreateEditTeamViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateBack by viewModel.navigateBack.collectAsStateWithLifecycle()

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
            CreateEditTeamContent(state = state, onEvent = viewModel::onEvent)
            if (state.showUnsavedChangesDialog) {
                UnsavedChangesDialog(
                    onConfirm = { viewModel.onEvent(CreateEditTeamUiEvent.ConfirmDiscard) },
                    onDismiss = { viewModel.onEvent(CreateEditTeamUiEvent.DismissUnsavedChangesDialog) }
                )
            }
        }
        is CreateEditTeamUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(state.message),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is CreateEditTeamUiState.Success -> {}
    }
}

@Composable
private fun UnsavedChangesDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val titleTextColor = MaterialTheme.colorScheme.onSurface

    val dialogDescriptionTextColor = MaterialTheme.colorScheme.onSecondaryContainer

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = MaterialTheme.colorScheme.surfaceContainer,
        shape            = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = stringResource(R.string.dialog_unsaved_changes_title), fontSize = 24.sp, fontWeight = FontWeight.Normal,
                color = titleTextColor
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_unsaved_changes_message), fontSize = 16.sp,
                color = dialogDescriptionTextColor
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dialog_unsaved_changes_cancel), fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary, fontSize = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.dialog_unsaved_changes_confirm), fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary, fontSize = 16.sp
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
    val titleSubtitleTextColors = MaterialTheme.colorScheme.onSurface

    val queryTextColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(top = 48.dp, bottom = 24.dp, start = 8.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onEvent(CreateEditTeamUiEvent.RequestBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.icon_back),
                            tint = titleSubtitleTextColors, modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = if (state.isEditMode) stringResource(R.string.edit_team_title) else stringResource(R.string.create_team_title),
                        fontSize = 28.sp, fontWeight = FontWeight.Medium, color = titleSubtitleTextColors
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {

                // ── Team name field — CREATE mode only ───────────────────────
                if (!state.isEditMode) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        TextField(
                            value = state.teamName,
                            onValueChange = { onEvent(CreateEditTeamUiEvent.NameChanged(it)) },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.create_team_name_placeholder), fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            },
                            label = { Text(text = stringResource(R.string.create_team_name_placeholder), color = MaterialTheme.colorScheme.outlineVariant) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = queryTextColor,
                                unfocusedTextColor = queryTextColor,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                errorContainerColor = MaterialTheme.colorScheme.error,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                            ),
                            textStyle  = LocalTextStyle.current.copy(fontSize = 16.sp),
                            isError    = state.nameError != null,
                            singleLine = true
                        )
                        if (state.nameError != null) {
                            Text(
                                text = stringResource(state.nameError),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                            )
                        }
                    }
                    item {
                        Text(
                            text = stringResource(R.string.create_team_add_people_section), fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = titleSubtitleTextColors,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }
                }

                // ── Search bar ───────────────────────────────────────────────
                item {
                    if (state.isEditMode) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { onEvent(CreateEditTeamUiEvent.SearchQueryChanged(it)) },
                        placeholder = {
                            Text(
                                text = stringResource(if (state.isEditMode) R.string.edit_team_search_placeholder else R.string.create_team_search_placeholder),
                                color = MaterialTheme.colorScheme.outlineVariant, fontSize = 16.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp)
                            .clip(RoundedCornerShape(28.dp)),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        trailingIcon = if (state.searchQuery.isNotBlank()) ({
                            IconButton(onClick = {
                                onEvent(CreateEditTeamUiEvent.SearchQueryChanged(""))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }) else null,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = queryTextColor,
                            unfocusedTextColor = queryTextColor,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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

                if (!state.isEditMode && state.suggestions.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.create_team_suggestions_section), fontSize = 16.sp,
                            color = titleSubtitleTextColors,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                        )
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(state.suggestions) { user ->
                                SuggestionItem(user = user, onClick = { onEvent(CreateEditTeamUiEvent.AddMember(user)) })
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (!state.isEditMode) {
                    item {
                        Text(
                            text = stringResource(R.string.create_team_member_list_section), fontSize = 16.sp, color = titleSubtitleTextColors,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
                        )
                    }
                }

                items(state.members) { member ->
                    MemberRow(user = member, isOwner = member.id == state.ownerID, onRemove = { onEvent(CreateEditTeamUiEvent.RemoveMember(member)) })
                }

                if (state.members.size <= 1) {
                    item {
                        Text(
                            text = stringResource(R.string.create_team_empty_hint), fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick        = { onEvent(CreateEditTeamUiEvent.Submit) },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(bottom = 24.dp, end = 24.dp).size(72.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor   = MaterialTheme.colorScheme.onSecondary,
            shape          = RoundedCornerShape(20.dp)
        ) {
            Icon(imageVector = if (state.isEditMode) Icons.Default.Check else Icons.Default.Add, contentDescription = stringResource(R.string.create_team_save_description), modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun SuggestionItem(user: ShareUser, onClick: () -> Unit) {
    val usernameColor = MaterialTheme.colorScheme.onBackground

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp).clickable { onClick() }) {
        UserAvatar(username = user.username, profilePictureURL = user.profilePictureURL, size = 64)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = user.username, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 2, lineHeight = 14.sp,
            color = usernameColor
        )
    }
}

@Composable
private fun SearchResultRow(user: ShareUser, onAdd: () -> Unit) {
    val titleSubtitleTextColors = MaterialTheme.colorScheme.onSurface

    val emailColor = MaterialTheme.colorScheme.primary

    Row(modifier = Modifier.fillMaxWidth().clickable { onAdd() }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        UserAvatar(username = user.username, profilePictureURL = user.profilePictureURL, size = 40)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = user.username, color = titleSubtitleTextColors)
            Text(text = user.email, fontSize = 12.sp, color = emailColor)
        }
    }
}

@Composable
private fun MemberRow(user: ShareUser, isOwner: Boolean, onRemove: () -> Unit) {
    val nameTextColor = MaterialTheme.colorScheme.onSurface

    val ownerEmailColor = MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(username = user.username, profilePictureURL = user.profilePictureURL, size = 52)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.username, fontSize = 18.sp, color = nameTextColor)
            Text(text = user.email, fontSize = 14.sp, color = ownerEmailColor)
        }
        if (isOwner) {
            Text(
                text = stringResource(R.string.teams_owner_label), color = primary, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(end = 8.dp)
            )
        } else {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.create_team_remove_member_description),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
