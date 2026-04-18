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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.domain.models.ShareAndPostState
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.ShareScreen.components.SelectedUserRow
import com.oracle.visualize.presentation.screens.ShareScreen.components.TeamRow
import com.oracle.visualize.presentation.screens.ShareScreen.components.TeamRowPosition
import com.oracle.visualize.presentation.screens.ShareScreen.components.UnsavedChangesDialog
import com.oracle.visualize.ui.theme.OrangeButton
import com.oracle.visualize.ui.theme.TealPrimary
import com.oracle.visualize.ui.theme.TextDark
import com.oracle.visualize.ui.theme.TextGray
import com.oracle.visualize.ui.theme.TopBarColor

private val SearchBarBg   = Color(0xFFF5F4F2)
private val SearchIconColor = Color(0xFF7FA9A9)



@Composable
fun ShareAndPostScreen(
    viewModel: ShareAndPostViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    bottomPadding: Dp = 0.dp
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
        onConfirmLeave = { viewModel.onConfirmLeave(); onNavigateBack() },
        bottomPadding = bottomPadding
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
    onConfirmLeave: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F2))
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
                Spacer(modifier = Modifier.height(12.dp))
                ShareUserList(state = state, onRemoveUser = onRemoveUser)
                Spacer(modifier = Modifier.height(16.dp))
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

        // AC-70: Dialog with dark overlay
        if (state.showUnsavedChangesDialog) {
            UnsavedChangesDialog(
                onDismiss = onDismissUnsavedChanges,
                onConfirmLeave = onConfirmLeave
            )
        }
    }
}


@Composable
private fun ShareTopBar(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFCDE9EA))
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(64.dp)
                .padding(start = 4.dp)
        ) {
            Box(
                modifier = Modifier.requiredSize(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .requiredWidth(40.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier.requiredSize(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1D1B20),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Text(
                text = "Share and post",
                color = Color(0xFF1D1B20),
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.29.em,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.requiredWidth(4.dp))
        }
    }
}


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
            .shadow(2.dp, RoundedCornerShape(28.dp))
            .background(SearchBarBg, RoundedCornerShape(28.dp))
            .padding(horizontal = 4.dp)
    ) {
        Box(modifier = Modifier.requiredSize(48.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SearchIconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Enter email",
                    color = SearchIconColor,
                    fontSize = 16.sp,
                    lineHeight = 1.5.em
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor             = TealPrimary
            ),
            modifier = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                lineHeight = 1.5.em
            )
        )
        Spacer(modifier = Modifier.requiredSize(48.dp))
    }
}


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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        visibleUsers.forEach { user ->
            key(user.id) {
                AnimatedVisibility(
                    visible = user in displayUsers,
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SelectedUserRow(user = user, onRemove = { onRemoveUser(user) })
                }
            }
        }
    }
}


@Composable
private fun ShareTeamSection(
    title: String,
    teams: List<ShareTeam>,
    emptyMessage: String,
    onToggleTeam: (String) -> Unit
) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = TextDark,
        lineHeight = 1.5.em,
        modifier = Modifier
            .requiredWidth(380.dp)
            .padding(bottom = 0.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (teams.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emptyMessage, fontSize = 13.sp, color = TextGray)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        teams.forEachIndexed { index, team ->
            val position = when {
                teams.size == 1 -> TeamRowPosition.SINGLE
                index == 0      -> TeamRowPosition.TOP
                index == teams.size - 1 -> TeamRowPosition.BOTTOM
                else            -> TeamRowPosition.MIDDLE
            }
            TeamRow(
                team = team,
                onToggle = { onToggleTeam(team.id) },
                position = position
            )
        }
    }
}


@Composable
private fun ShareBottomBar(onConfirmShare: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(136.dp)
            .background(Color(0xFFCDE9EA))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onConfirmShare,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEB9632),
                contentColor = Color.White
            ),
            modifier = Modifier
                .requiredWidth(184.dp)
                .requiredHeight(56.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
