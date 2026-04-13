package com.oracle.visualize.ui.sharing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Color tokens ─────────────────────────────────────────────────────────────
private val BackgroundColor  = Color(0xFFDDE8EA)
private val SurfaceColor     = Color(0xFFFFFFFF)
private val TealSelected     = Color(0xFF3D8C84)
private val OrangeConfirm    = Color(0xFFE8A85C)
private val SearchBarBg      = Color(0xFFECF3F4)
private val TextPrimary      = Color(0xFF1A1A2E)
private val TextSecondary    = Color(0xFF8A8A9A)
private val RemoveRed        = Color(0xFFE05555)
private val SectionHeader    = Color(0xFF2C2C3E)
private val CardBackground   = Color(0xFFF5F7F8)

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
            viewModel.onBackPressed()
            if (!state.showUnsavedChangesDialog) onNavigateBack()
        },
        onDismissUnsavedChanges = viewModel::onDismissUnsavedChanges,
        onConfirmLeave = {
            viewModel.onConfirmLeave()
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .background(BackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ──────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor)
                    .padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Share and post",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // ── Scrollable content ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                // ── Email search bar ─────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(SearchBarBg)
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = state.emailQuery,
                        onValueChange = onEmailQueryChange,
                        placeholder = {
                            Text("Enter email", color = TextSecondary, fontSize = 14.sp)
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = TealSelected
                        ),
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ── Selected users list ──────────────────────────────────────
                if (state.selectedUsers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    state.selectedUsers.forEach { user ->
                        SelectedUserRow(user = user, onRemove = { onRemoveUser(user) })
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select teammates individually, or choose a team below.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── My Teams section ─────────────────────────────────────────
                Text(
                    text = "My Teams",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SectionHeader,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                if (state.myTeams.isEmpty()) {
                    EmptyStateText("You haven't created any teams yet.")
                } else {
                    state.myTeams.forEach { team ->
                        TeamRow(team = team, onToggle = { onToggleTeam(team.id, true) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Teams I'm In section ─────────────────────────────────────
                Text(
                    text = "Teams I'm In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SectionHeader,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                if (state.teamsImIn.isEmpty()) {
                    EmptyStateText("You're not part of any teams yet.")
                } else {
                    state.teamsImIn.forEach { team ->
                        TeamRow(team = team, onToggle = { onToggleTeam(team.id, false) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // ── Bottom confirm button ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onConfirmShare,
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeConfirm,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // ── Unsaved changes dialog ───────────────────────────────────────────
        if (state.showUnsavedChangesDialog) {
            UnsavedChangesDialog(
                onDismiss = onDismissUnsavedChanges,
                onConfirmLeave = onConfirmLeave
            )
        }
    }
}

// ─── Selected user row ────────────────────────────────────────────────────────
@Composable
private fun SelectedUserRow(user: ShareUser, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        UserAvatar(
            initials = user.avatarInitials,
            color = Color(user.avatarColor),
            size = 42
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = user.email,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = RemoveRed,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Team row ─────────────────────────────────────────────────────────────────
@Composable
private fun TeamRow(team: ShareTeam, onToggle: () -> Unit) {
    val bgColor   = if (team.isSelected) TealSelected else CardBackground
    val textColor = if (team.isSelected) Color.White else TextPrimary
    val subColor  = if (team.isSelected) Color.White.copy(alpha = 0.85f) else TextSecondary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                text = "${team.memberCount} members",
                fontSize = 12.sp,
                color = subColor
            )
        }
        MemberAvatarStack(members = team.members, isSelected = team.isSelected)
    }
}

// ─── Member avatar stack ──────────────────────────────────────────────────────
@Composable
private fun MemberAvatarStack(members: List<ShareUser>, isSelected: Boolean) {
    val avatarColors = listOf(
        Color(0xFFE8A87C),
        Color(0xFF7EC8C8),
        Color(0xFFB8D4E8),
        Color(0xFFE8C87C),
        Color(0xFF8CB87C)
    )
    val displayCount = minOf(members.size, 3)
    val extraCount   = members.size - displayCount

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width((displayCount * 22 + 8).dp).height(28.dp)) {
            repeat(displayCount) { index ->
                Box(
                    modifier = Modifier
                        .offset(x = (index * 18).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(avatarColors[index % avatarColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = members.getOrNull(index)?.avatarInitials ?: "",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        if (extraCount > 0) {
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White.copy(alpha = 0.3f) else Color(0xFFCDD5D8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$extraCount",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
        }
    }
}

// ─── User avatar ──────────────────────────────────────────────────────────────
@Composable
private fun UserAvatar(initials: String, color: Color, size: Int = 38) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = (size * 0.37f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────
@Composable
private fun EmptyStateText(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 13.sp, color = TextSecondary)
    }
}

// ─── Unsaved changes dialog ───────────────────────────────────────────────────
@Composable
private fun UnsavedChangesDialog(onDismiss: () -> Unit, onConfirmLeave: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text("Unsaved Changes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        },
        text = {
            Text(
                "You have unsaved changes. Are you sure you want to leave?",
                fontSize = 14.sp,
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmLeave) {
                Text("Share", color = TealSelected, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShareAndPostEmptyPreview() {
    MaterialTheme {
        ShareAndPostContent(
            state = ShareAndPostState(),
            onEmailQueryChange = {}, onAddUserByEmail = {}, onRemoveUser = {},
            onToggleTeam = { _, _ -> }, onConfirmShare = {}, onBackPressed = {},
            onDismissUnsavedChanges = {}, onConfirmLeave = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShareAndPostWithUsersPreview() {
    MaterialTheme {
        ShareAndPostContent(
            state = ShareAndPostState(
                selectedUsers = listOf(
                    ShareUser("1", "Diana Escalante", "dianaescalante@gmail.com", "D", 0xFFE8A87C),
                    ShareUser("2", "Jocelyn Duarte", "jocelynduarte@gmail.com", "J", 0xFF7EC8C8),
                    ShareUser("3", "Eduardo Salazar", "eduardosalazar@gmail.com", "E", 0xFF8CB87C)
                ),
                myTeams = listOf(
                    ShareTeam("t1", "Data Analyst", 2, listOf(
                        ShareUser("u1", "A", "a@g.com", "A", 0xFFE8A87C),
                        ShareUser("u2", "B", "b@g.com", "B", 0xFF7EC8C8)
                    )),
                    ShareTeam("t2", "Data Analyst", 5, listOf(
                        ShareUser("u1", "A", "a@g.com", "A", 0xFFE8A87C),
                        ShareUser("u2", "B", "b@g.com", "B", 0xFF7EC8C8),
                        ShareUser("u3", "C", "c@g.com", "C", 0xFFB8D4E8)
                    ), isSelected = true)
                ),
                teamsImIn = listOf(
                    ShareTeam("t3", "Data Analyst", 5, listOf(
                        ShareUser("u4", "D", "d@g.com", "D", 0xFFE8C87C),
                        ShareUser("u5", "E", "e@g.com", "E", 0xFF8CB87C)
                    )),
                    ShareTeam("t4", "Data Analyst", 5, listOf(
                        ShareUser("u4", "D", "d@g.com", "D", 0xFFE8C87C),
                        ShareUser("u5", "E", "e@g.com", "E", 0xFF8CB87C),
                        ShareUser("u6", "F", "f@g.com", "F", 0xFFB87C8C)
                    ), isSelected = true)
                )
            ),
            onEmailQueryChange = {}, onAddUserByEmail = {}, onRemoveUser = {},
            onToggleTeam = { _, _ -> }, onConfirmShare = {}, onBackPressed = {},
            onDismissUnsavedChanges = {}, onConfirmLeave = {}
        )
    }
}