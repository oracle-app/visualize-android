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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.oracle.visualize.ui.theme.LightBlue
import com.oracle.visualize.ui.theme.StrongOrange
import com.oracle.visualize.ui.theme.VeryLightGray

/**
 * Entry-point composable for the Create/Edit Team screen.
 * Observes [CreateEditTeamViewModel] and navigates back automatically on success.
 *
 * @param onNavigateBack Callback invoked when the operation completes or the user taps back.
 * @param viewModel The [CreateEditTeamViewModel] instance (injected by Hilt).
 */
@Composable
fun CreateEditTeamPage(
    onNavigateBack: () -> Unit,
    viewModel: CreateEditTeamViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is CreateEditTeamUiState.Success) {
            onNavigateBack()
        }
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
                onEvent = viewModel::onEvent,
                onBack  = onNavigateBack
            )
        }
        is CreateEditTeamUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is CreateEditTeamUiState.Success -> {
            // Handled by LaunchedEffect above
        }
    }
}

@Composable
private fun CreateEditTeamContent(
    state: CreateEditTeamUiState.Content,
    onEvent: (CreateEditTeamUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue)
                    .padding(top = 48.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.icon_back)
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

                // Team name input (create mode only)
                if (!state.isEditMode) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(VeryLightGray)
                        ) {
                            TextField(
                                value         = state.teamName,
                                onValueChange = { onEvent(CreateEditTeamUiEvent.NameChanged(it)) },
                                placeholder   = {
                                    Text(
                                        stringResource(R.string.create_team_name_placeholder),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier      = Modifier.fillMaxSize(),
                                colors        = TextFieldDefaults.colors(
                                    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    focusedIndicatorColor   = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                                ),
                                isError       = state.nameError != null,
                                singleLine    = true
                            )
                        }
                        // Name validation error
                        if (state.nameError != null) {
                            Text(
                                text     = state.nameError,
                                color    = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }

                // Add people section
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        if (!state.isEditMode) {
                            Text(
                                text       = stringResource(R.string.create_team_add_people_section),
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Search bar
                        TextField(
                            value         = state.searchQuery,
                            onValueChange = { onEvent(CreateEditTeamUiEvent.SearchQueryChanged(it)) },
                            placeholder   = {
                                Text(
                                    if (state.isEditMode) stringResource(R.string.edit_team_search_placeholder)
                                    else stringResource(R.string.create_team_search_placeholder),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier      = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            leadingIcon   = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors        = TextFieldDefaults.colors(
                                focusedContainerColor   = VeryLightGray,
                                unfocusedContainerColor = VeryLightGray,
                                focusedIndicatorColor   = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                            ),
                            singleLine    = true
                        )
                    }
                }

                // Search results
                if (state.searchQuery.isNotBlank() && state.searchResults.isNotEmpty()) {
                    items(state.searchResults) { user ->
                        SearchResultRow(
                            user  = user,
                            onAdd = { onEvent(CreateEditTeamUiEvent.AddMember(user)) }
                        )
                    }
                }

                // Suggestions horizontal row (create mode, not searching)
                if (!state.isEditMode && state.searchQuery.isBlank()) {
                    item {
                        Column {
                            Text(
                                text       = stringResource(R.string.create_team_suggestions_section),
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                            LazyRow(
                                contentPadding      = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.suggestions) { user ->
                                    SuggestionItem(
                                        user    = user,
                                        onClick = { onEvent(CreateEditTeamUiEvent.AddMember(user)) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Member list header
                item {
                    Text(
                        text       = stringResource(R.string.create_team_member_list_section),
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }

                items(state.members) { member ->
                    MemberRow(
                        user     = member,
                        isOwner  = member.id == state.ownerID,
                        onRemove = { onEvent(CreateEditTeamUiEvent.RemoveMember(member)) }
                    )
                }
            }
        }

        // FAB — submit
        FloatingActionButton(
            onClick        = { onEvent(CreateEditTeamUiEvent.Submit) },
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .size(80.dp),
            containerColor = StrongOrange,
            contentColor   = MaterialTheme.colorScheme.onPrimary,
            shape          = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Check,
                contentDescription = stringResource(R.string.create_team_save_description),
                modifier           = Modifier.size(40.dp)
            )
        }
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
            Text(text = user.username, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = user.email,    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SuggestionItem(user: ShareUser, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        UserAvatar(user = user, size = 64)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text      = user.username,
            fontSize  = 12.sp,
            textAlign = TextAlign.Center,
            maxLines  = 2,
            lineHeight = 14.sp,
            color     = MaterialTheme.colorScheme.onSurface
        )
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
        UserAvatar(user = user, size = 50)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.username, fontWeight = FontWeight.Normal, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = user.email,    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
