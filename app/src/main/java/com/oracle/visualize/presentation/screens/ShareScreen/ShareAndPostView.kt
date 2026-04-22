package com.oracle.visualize.presentation.screens.ShareScreen

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.ShareScreen.components.*
import com.oracle.visualize.ui.theme.AppColors

// ─── Entry point ──────────────────────────────────────────────────────────────

@Composable
fun ShareAndPostScreen(
    viewModel: ShareAndPostViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load mock data as if it came from a Repository — ViewModel stays clean
    LaunchedEffect(Unit) {
        viewModel.loadData(
            myTeams      = ShareMockData.myTeams,
            teamsImIn    = ShareMockData.teamsImIn,
            suggestedUsers = ShareMockData.suggestedUsers
        )
    }

    when (val state = uiState) {
        is ShareUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(AppColors.screenBackground),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.tealPrimary)
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
            Box(modifier = Modifier.fillMaxSize().background(AppColors.screenBackground),
                contentAlignment = Alignment.Center) {
                Text(text = state.message, color = AppColors.errorRed)
            }
        }
    }
}

// ─── Main content ─────────────────────────────────────────────────────────────

@Composable
fun ShareAndPostContent(
    state: ShareUiState.Content,
    onEvent: (ShareUiEvent) -> Unit   // Single event callback replaces 9+ lambdas
) {
    Box(modifier = Modifier.fillMaxSize().background(AppColors.screenBackground)) {
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
                Spacer(modifier = Modifier.height(12.dp))
                ShareUserList(state = state, onEvent = onEvent)
                Spacer(modifier = Modifier.height(16.dp))
                ShareTeamSection(
                    title = "My Teams",
                    teams = state.myTeams,
                    selectedTeamIds = state.selectedTeamIds,
                    emptyMessage = "You haven't created any teams yet.",
                    onToggleTeam = { id -> onEvent(ShareUiEvent.ToggleTeam(id, isMyTeam = true)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShareTeamSection(
                    title = "Teams I'm In",
                    teams = state.teamsImIn,
                    selectedTeamIds = state.selectedTeamIds,
                    emptyMessage = "You're not part of any teams yet.",
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
    Column(modifier = Modifier.fillMaxWidth().background(AppColors.topBarColor)) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().requiredHeight(64.dp).padding(start = 4.dp)
        ) {
            Box(modifier = Modifier.requiredSize(48.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.requiredWidth(40.dp).clip(RoundedCornerShape(100.dp)), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onBackPressed, modifier = Modifier.requiredSize(40.dp)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.textDark, modifier = Modifier.size(24.dp))
                    }
                }
            }
            Text(
                text = "Share and post",
                color = AppColors.textDark,
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
            .background(AppColors.searchBarBg)
            .padding(horizontal = 4.dp)
    ) {
        Box(modifier = Modifier.requiredSize(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AppColors.textMuted, modifier = Modifier.size(24.dp))
        }
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Enter email", color = AppColors.textMuted, fontSize = 16.sp) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor             = AppColors.tealPrimary,
                focusedTextColor        = AppColors.textDark,
                unfocusedTextColor      = AppColors.textDark
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
    val displayUsers = if (state.selectedUsers.isNotEmpty()) state.selectedUsers else state.suggestedUsers
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
        color = AppColors.sectionTitle, lineHeight = 1.5.em,
        modifier = Modifier.requiredWidth(380.dp))
    Spacer(modifier = Modifier.height(8.dp))

    if (teams.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(text = emptyMessage, fontSize = 13.sp, color = AppColors.textGray)
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
            .background(AppColors.topBarColor).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onConfirmShare,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.orangeButton, contentColor = Color.White),
            modifier = Modifier.requiredWidth(184.dp).requiredHeight(56.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}