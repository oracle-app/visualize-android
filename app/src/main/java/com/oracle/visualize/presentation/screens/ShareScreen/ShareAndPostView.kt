package com.oracle.visualize.presentation.screens.ShareScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.domain.models.ShareAndPostState
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.ShareScreen.components.SelectedUserRow
import com.oracle.visualize.presentation.screens.ShareScreen.components.TeamRow
import com.oracle.visualize.presentation.screens.ShareScreen.components.UnsavedChangesDialog
import com.oracle.visualize.ui.theme.OrangeButton
import com.oracle.visualize.ui.theme.TealPrimary
import com.oracle.visualize.ui.theme.TextDark
import com.oracle.visualize.ui.theme.TextGray
import com.oracle.visualize.ui.theme.TopBarColor

private val SearchBarBg = Color(0xFFF5F5F5)
private val SearchIconColor = Color(0xFF7FA9A9)

@Composable
fun ShareAndPostScreen(
    viewModel: ShareAndPostViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ShareAndPostContent(
        state = state,
        onEmailQueryChange = viewModel::onEmailQueryChange,
        onAddUserByEmail = viewModel::onAddUserByEmail,
        onRemoveUser = viewModel::onRemoveUser,
        onToggleTeam = viewModel::onToggleTeam,
        onConfirmShare = viewModel::onConfirmShare,
        onBackPressed = {
            val hadSelections = state.selectedUsers.isNotEmpty() ||
                    state.myTeams.any { it.isSelected } ||
                    state.teamsImIn.any { it.isSelected }
            if (hadSelections) viewModel.onBackPressed() else onNavigateBack()
        },
        onDismissUnsavedChanges = viewModel::onDismissUnsavedChanges,
        onConfirmLeave = { viewModel.onConfirmLeave(); onNavigateBack() }
    )
}

@Composable
fun ShareAndPostContent(
    state: ShareAndPostState,
    onEmailQueryChange: (String) -> Unit,
    onAddUserByEmail: (String) -> Unit,
    onRemoveUser: (ShareUser) -> Unit,
    onToggleTeam: (String, Boolean) -> Unit,
    onConfirmShare: () -> Unit,
    onBackPressed: () -> Unit,
    onDismissUnsavedChanges: () -> Unit,
    onConfirmLeave: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ShareTopBar(onBackPressed = onBackPressed)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ShareSearchBar(
                    query = state.emailQuery,
                    onQueryChange = onEmailQueryChange
                )
                Spacer(modifier = Modifier.height(10.dp))
                ShareUserList(
                    state = state,
                    onRemoveUser = onRemoveUser
                )
                Spacer(modifier = Modifier.height(24.dp))
                ShareTeamSection(
                    title = "My Teams",
                    teams = state.myTeams,
                    emptyMessage = "You haven't created any teams yet.",
                    onToggleTeam = { id -> onToggleTeam(id, true) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShareTeamSection(
                    title = "Teams I'm In",
                    teams = state.teamsImIn,
                    emptyMessage = "You're not part of any teams yet.",
                    onToggleTeam = { id -> onToggleTeam(id, false) }
                )
                Spacer(modifier = Modifier.height(80.dp))
            }

            ShareBottomBar(onConfirmShare = onConfirmShare)
        }

        if (state.showUnsavedChangesDialog) {
            UnsavedChangesDialog(
                onDismiss = onDismissUnsavedChanges,
                onConfirmLeave = onConfirmLeave
            )
        }
    }
}

// ─── Top bar ─────────────────────────────────────────────────────────────────

@Composable
private fun ShareTopBar(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(TopBarColor)
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBackPressed, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1D1B20),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Share and post",
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1D1B20),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            )
        }
    }
}

// ─── Search bar ───────────────────────────────────────────────────────────────

@Composable
private fun ShareSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(1.dp, RoundedCornerShape(28.dp))
            .background(SearchBarBg, RoundedCornerShape(28.dp))
            .padding(horizontal = 4.dp)
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SearchIconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text("Enter email", color = TextGray, fontSize = 14.sp)
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = TealPrimary
            ),
            modifier = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )
    }
}

// ─── User list ────────────────────────────────────────────────────────────────

@Composable
private fun ShareUserList(
    state: ShareAndPostState,
    onRemoveUser: (ShareUser) -> Unit
) {
    val displayUsers = if (state.selectedUsers.isNotEmpty()) state.selectedUsers
    else state.suggestedUsers

    if (displayUsers.isEmpty()) return

    val visibleUsers = remember { mutableStateListOf(*displayUsers.toTypedArray()) }
    LaunchedEffect(displayUsers) {
        visibleUsers.clear()
        visibleUsers.addAll(displayUsers)
    }

    visibleUsers.forEach { user ->
        key(user.id) {
            AnimatedVisibility(
                visible = user in displayUsers,
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    SelectedUserRow(user = user, onRemove = { onRemoveUser(user) })
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

// ─── Team section ─────────────────────────────────────────────────────────────

@Composable
private fun ShareTeamSection(
    title: String,
    teams: List<com.oracle.visualize.domain.models.ShareTeam>,
    emptyMessage: String,
    onToggleTeam: (String) -> Unit
) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        modifier = Modifier.padding(bottom = 10.dp)
    )
    if (teams.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emptyMessage, fontSize = 13.sp, color = TextGray)
        }
    } else {
        teams.forEach { team ->
            TeamRow(team = team, onToggle = { onToggleTeam(team.id) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ─── Bottom bar ───────────────────────────────────────────────────────────────

@Composable
private fun ShareBottomBar(onConfirmShare: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(TopBarColor)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onConfirmShare,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeButton,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            modifier = Modifier
                .width(184.dp)
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Confirm",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp
            )
        }
    }
}