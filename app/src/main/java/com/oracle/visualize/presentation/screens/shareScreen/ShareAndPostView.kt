package com.oracle.visualize.presentation.screens.shareScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.presentation.screens.shareScreen.components.*
import com.oracle.visualize.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource


@Composable
fun ShareAndPostScreen(
    viewModel: ShareAndPostViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load mock data as if it came from a Repository — ViewModel stays clean

    when (val state = uiState) {
        is ShareUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is ShareUiState.Content -> {
            ShareAndPostContent(
                state = state,
                onEvent = { event ->
                    // Navigation side effects handled here, not in ViewModel
                    when (event) {
                        is ShareUiEvent.BackPressed -> {
                            if (!state.hasChanges) onNavigateBack()
                            else viewModel.onEvent(event)
                        }
                        is ShareUiEvent.ConfirmLeave -> {
                            viewModel.onEvent(event)
                            onNavigateBack()
                        }
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
        is ShareUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Composable
fun ShareAndPostContent(
    state: ShareUiState.Content,
    onEvent: (ShareUiEvent) -> Unit   // Single event callback replaces 9+ lambdas
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            ShareTopBar(onBackPressed = { onEvent(ShareUiEvent.BackPressed) })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ShareSearchBar(
                    query = state.emailQuery,
                    onQueryChange = { onEvent(ShareUiEvent.EmailQueryChanged(it)) }
                )
                if (state.suggestedUsers.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)
                        state.suggestedUsers.forEach { user ->
                            SuggestedUserRow(user = user) {
                                onEvent(ShareUiEvent.AddUserByEmail(user.email))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ShareUserList(state = state, onEvent = onEvent)
                Spacer(modifier = Modifier.height(16.dp))
                ShareTeamSection(
                    title = stringResource(R.string.my_teams),
                    teams = state.myTeams,
                    selectedTeamIds = state.selectedTeamIds,
                    emptyMessage = stringResource(R.string.empty_team),
                    onToggleTeam = { id -> onEvent(ShareUiEvent.ToggleTeam(id, isMyTeam = true)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShareTeamSection(
                    title = stringResource(R.string.share_my_teams),
                    teams = state.teamsImIn,
                    selectedTeamIds = state.selectedTeamIds,
                    emptyMessage = stringResource(R.string.share_empty_teams),
                    onToggleTeam = { id -> onEvent(ShareUiEvent.ToggleTeam(id, isMyTeam = false)) }
                )
                Spacer(modifier = Modifier.height(80.dp))
            }

            ShareBottomBar(onConfirmShare = { onEvent(ShareUiEvent.ConfirmShare) })
        }

        if (state.showUnsavedChangesDialog) {
            UnsavedChangesDialog(
                onDismiss = { onEvent(ShareUiEvent.DismissUnsavedChanges) },
                onConfirmLeave = { onEvent(ShareUiEvent.ConfirmLeave) }
            )
        }
    }
}

// ─── Top bar ──────────────────────────────────────────────────────────────────

@Composable
private fun ShareTopBar(onBackPressed: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).statusBarsPadding()) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().requiredHeight(64.dp).padding(start = 4.dp)
        ) {
            Box(modifier = Modifier.requiredSize(48.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.requiredWidth(40.dp).clip(RoundedCornerShape(100.dp)), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onBackPressed, modifier = Modifier.requiredSize(40.dp)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                    }
                }
            }
            Text(
                text = stringResource(R.string.share_and_post),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.29.em,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).wrapContentHeight(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.requiredWidth(4.dp))
        }
    }
}

// ─── Search bar ───────────────────────────────────────────────────────────────

@Composable
private fun ShareSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth().height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp)
    ) {
        Box(modifier = Modifier.requiredSize(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(24.dp))
        }
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.input_email), color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 16.sp) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor             = MaterialTheme.colorScheme.primary,
                focusedTextColor        = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedTextColor      = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.requiredSize(48.dp))
    }
}

// ─── User list ────────────────────────────────────────────────────────────────

@Composable
private fun ShareUserList(state: ShareUiState.Content, onEvent: (ShareUiEvent) -> Unit) {
    val displayUsers = state.selectedUsers
    if (displayUsers.isEmpty()) return

    val visibleUsers = remember { mutableStateListOf(*displayUsers.toTypedArray()) }
    LaunchedEffect(displayUsers) { visibleUsers.clear(); visibleUsers.addAll(displayUsers) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        visibleUsers.forEach { user ->
            key(user.id) {
                AnimatedVisibility(visible = user in displayUsers, exit = shrinkVertically() + fadeOut()) {
                    SelectedUserRow(user = user, onRemove = { onEvent(ShareUiEvent.RemoveUser(user)) })
                }
            }
        }
    }
}

// ─── Team section ─────────────────────────────────────────────────────────────

@Composable
private fun ShareTeamSection(
    title: String,
    teams: List<ShareTeam>,
    selectedTeamIds: Set<String>,   // Selection state lives here, not in the entity
    emptyMessage: String,
    onToggleTeam: (String) -> Unit
) {
    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onPrimaryContainer, lineHeight = 1.5.em,
        modifier = Modifier.requiredWidth(380.dp))
    Spacer(modifier = Modifier.height(8.dp))

    if (teams.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(text = emptyMessage, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        teams.forEachIndexed { index, team ->
            val position = when {
                teams.size == 1         -> TeamRowPosition.SINGLE
                index == 0              -> TeamRowPosition.TOP
                index == teams.size - 1 -> TeamRowPosition.BOTTOM
                else                    -> TeamRowPosition.MIDDLE
            }
            TeamRow(
                team = team,
                isSelected = team.id in selectedTeamIds,  // selection from state, not entity
                onToggle = { onToggleTeam(team.id) },
                position = position
            )
        }
    }
}

// ─── Bottom bar ───────────────────────────────────────────────────────────────

@Composable
private fun ShareBottomBar(onConfirmShare: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().requiredHeight(136.dp)
            .background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onConfirmShare,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.White),
            modifier = Modifier.requiredWidth(184.dp).requiredHeight(56.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.confirm), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

