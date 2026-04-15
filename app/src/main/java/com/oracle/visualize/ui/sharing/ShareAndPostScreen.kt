package com.oracle.visualize.ui.sharing

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Colors ───────────────────────────────────────────────────────────────────
private val TopBarColor     = Color(0xFFB8D8DC)
private val BackgroundColor = Color(0xFFDDE8EA)
private val SurfaceColor    = Color(0xFFFFFFFF)
private val TealSelected    = Color(0xFF3D8C84)
private val OrangeConfirm   = Color(0xFFE8A85C)
private val SearchBarBg     = Color(0xFFECF3F4)
private val TextPrimary     = Color(0xFF1A1A2E)
private val TextSecondary   = Color(0xFF8A8A9A)
private val RemoveRed       = Color(0xFFE05555)

// ─── Mock data para preview ───────────────────────────────────────────────────
val mockSelectedUsers = listOf(
    ShareUser("1", "Diana Escalante", "dianaescalante@gmail.com", "D", 0xFFE8A87C),
    ShareUser("2", "Jocelyn Duarte",  "jocelynduarte@gmail.com",  "J", 0xFF7EC8C8),
    ShareUser("3", "Eduardo Salazar", "eduardosalazar@gmail.com", "E", 0xFF8CB87C)
)

val mockMyTeams = listOf(
    ShareTeam("t1", "Data Analyst", 2, listOf(
        ShareUser("u1", "Ana",  "ana@gmail.com",  "A", 0xFFE8A87C),
        ShareUser("u2", "Bob",  "bob@gmail.com",  "B", 0xFF7EC8C8)
    )),
    ShareTeam("t2", "Data Analyst", 5, listOf(
        ShareUser("u1", "Ana",  "ana@gmail.com",  "A", 0xFFE8A87C),
        ShareUser("u2", "Bob",  "bob@gmail.com",  "B", 0xFF7EC8C8),
        ShareUser("u3", "Carl", "carl@gmail.com", "C", 0xFFB8D4E8),
        ShareUser("u7", "Dana", "dana@gmail.com", "D", 0xFFE8C87C),
        ShareUser("u8", "Erik", "erik@gmail.com", "E", 0xFF8CB87C)
    ))
)

val mockTeamsImIn = listOf(
    ShareTeam("t3", "Data Analyst", 5, listOf(
        ShareUser("u4", "Diana", "diana@gmail.com", "D", 0xFFE8C87C),
        ShareUser("u5", "Eve",   "eve@gmail.com",   "E", 0xFF8CB87C),
        ShareUser("u9", "Frank", "frank@gmail.com", "F", 0xFFB87C8C)
    )),
    ShareTeam("t4", "Data Analyst", 5, listOf(
        ShareUser("u4",  "Diana", "diana@gmail.com", "D", 0xFFE8C87C),
        ShareUser("u5",  "Eve",   "eve@gmail.com",   "E", 0xFF8CB87C),
        ShareUser("u6",  "Frank", "frank@gmail.com", "F", 0xFFB87C8C),
        ShareUser("u10", "Gina",  "gina@gmail.com",  "G", 0xFF9CB8E8),
        ShareUser("u11", "Hank",  "hank@gmail.com",  "H", 0xFFE89CB8)
    ))
)

// ─── Screen entry point ───────────────────────────────────────────────────────
@Composable
fun ShareAndPostScreen(
    viewModel: ShareAndPostViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ShareAndPostContent(
        state                   = state,
        onEmailQueryChange      = viewModel::onEmailQueryChange,
        onAddUserByEmail        = viewModel::onAddUserByEmail,
        onRemoveUser            = viewModel::onRemoveUser,
        onToggleTeam            = viewModel::onToggleTeam,
        onConfirmShare          = viewModel::onConfirmShare,
        onBackPressed           = {
            viewModel.onBackPressed()
            if (!state.showUnsavedChangesDialog) onNavigateBack()
        },
        onDismissUnsavedChanges = viewModel::onDismissUnsavedChanges,
        onConfirmLeave          = { viewModel.onConfirmLeave(); onNavigateBack() }
    )
}

// ─── Main content ─────────────────────────────────────────────────────────────
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .background(Color(0xFFCDE9EA))
            ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // ── Back button (Content) ──────────────────────────────────
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // State-layer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onBackPressed,
                                modifier = Modifier.size(40.dp)
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

                    Spacer(modifier = Modifier.width(4.dp))

                    // ── Text Content ───────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .width(340.dp)
                            .wrapContentHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Headline
                        Text(
                            text = "Share and post",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 36.sp,
                            color = Color(0xFF1D1B20),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ── Contenido scrollable ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // ── Barra de búsqueda ────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(28.dp)) // ✅ sombra
                        .background(SurfaceColor, RoundedCornerShape(28.dp))
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
                            focusedContainerColor   = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor             = TealSelected
                        ),
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Usuarios sugeridos / seleccionados ───────────────────────
                val displayUsers = if (state.selectedUsers.isNotEmpty())
                    state.selectedUsers else state.suggestedUsers

                if (displayUsers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
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
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // ── My Teams ─────────────────────────────────────────────────
                Text(
                    text = "My Teams",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                if (state.myTeams.isEmpty()) {
                    EmptyStateText("You haven't created any teams yet.")
                } else {
                    state.myTeams.forEach { team ->
                        TeamRow(team = team, onToggle = { onToggleTeam(team.id, true) })
                        Spacer(modifier = Modifier.height(10.dp)) // ✅ más espacio
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Teams I'm In ─────────────────────────────────────────────
                Text(
                    text = "Teams I'm In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                if (state.teamsImIn.isEmpty()) {
                    EmptyStateText("You're not part of any teams yet.")
                } else {
                    state.teamsImIn.forEach { team ->
                        TeamRow(team = team, onToggle = { onToggleTeam(team.id, false) })
                        Spacer(modifier = Modifier.height(10.dp)) // ✅ más espacio
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

        // ── Barra inferior + botón Confirm ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(136.dp)
                    .background(TopBarColor)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onConfirmShare,
                    shape = RoundedCornerShape(16.dp), // ✅ border-radius: 16px
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEB9632), // ✅ background: #EB9632
                        contentColor   = Color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp, // ✅ padding: 16px 24px
                        vertical   = 16.dp
                    ),
                    modifier = Modifier
                        .width(184.dp)  // ✅ width: 184px
                        .height(56.dp)  // ✅ height: 56px
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // ✅ icon: 24x24
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // ✅ gap: 8px
                    Text(
                        text = "Confirm",
                        fontSize = 16.sp,           // ✅ font-size: 16px
                        fontWeight = FontWeight.Medium, // ✅ font-weight: 500
                        letterSpacing = 0.15.sp,    // ✅ letter-spacing: 0.15px
                        lineHeight = 24.sp,         // ✅ line-height: 24px
                        color = Color.White
                    )
                }
            }
        }

        // ── Diálogo cambios sin guardar ──────────────────────────────────────
        if (state.showUnsavedChangesDialog) {
            UnsavedChangesDialog(
                onDismiss      = onDismissUnsavedChanges,
                onConfirmLeave = onConfirmLeave
            )
        }
    }
}

// ─── Fila de usuario seleccionado ─────────────────────────────────────────────
@Composable
private fun SelectedUserRow(user: ShareUser, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)) // ✅ sombra
            .background(SurfaceColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp) // ✅ más padding vertical
    ) {
        UserAvatar(initials = user.avatarInitials, color = Color(user.avatarColor), size = 42)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                user.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                user.email,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = RemoveRed,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Fila de equipo ───────────────────────────────────────────────────────────
@Composable
private fun TeamRow(team: ShareTeam, onToggle: () -> Unit) {
    val bgColor   = if (team.isSelected) TealSelected else SurfaceColor // ✅ blanco puro
    val textColor = if (team.isSelected) Color.White  else TextPrimary
    val subColor  = if (team.isSelected) Color.White.copy(alpha = 0.85f) else TextSecondary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)) // ✅ sombra
            .background(bgColor, RoundedCornerShape(14.dp))
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 14.dp) // ✅ más padding vertical
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                team.name,
                fontSize = 15.sp, // ✅ ligeramente más grande
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                "${team.memberCount} members",
                fontSize = 12.sp,
                color = subColor
            )
        }
        MemberAvatarStack(members = team.members, isSelected = team.isSelected)
    }
}

// ─── Stack de avatares ────────────────────────────────────────────────────────
@Composable
private fun MemberAvatarStack(members: List<ShareUser>, isSelected: Boolean) {
    val colors = listOf(
        Color(0xFFE8A87C), Color(0xFF7EC8C8), Color(0xFFB8D4E8),
        Color(0xFFE8C87C), Color(0xFF8CB87C)
    )
    val displayCount = minOf(members.size, 3)
    val extraCount   = members.size - displayCount

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width((displayCount * 20 + 8).dp)
                .height(28.dp)
        ) {
            repeat(displayCount) { index ->
                Box(
                    modifier = Modifier
                        .offset(x = (index * 16).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colors[index % colors.size]),
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
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.25f)
                        else Color(0xFFCDD5D8)
                    ),
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

// ─── Avatar individual ────────────────────────────────────────────────────────
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

// ─── Estado vacío ─────────────────────────────────────────────────────────────
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

// ─── Diálogo cambios sin guardar ──────────────────────────────────────────────
@Composable
private fun UnsavedChangesDialog(onDismiss: () -> Unit, onConfirmLeave: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceColor,
        shape            = RoundedCornerShape(16.dp),
        title = {
            Text(
                "Unsaved Changes",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
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
            state = ShareAndPostState(myTeams = mockMyTeams, teamsImIn = mockTeamsImIn),
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
                selectedUsers = mockSelectedUsers,
                myTeams       = mockMyTeams,
                teamsImIn     = mockTeamsImIn
            ),
            onEmailQueryChange = {}, onAddUserByEmail = {}, onRemoveUser = {},
            onToggleTeam = { _, _ -> }, onConfirmShare = {}, onBackPressed = {},
            onDismissUnsavedChanges = {}, onConfirmLeave = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShareAndPostWithSelectionPreview() {
    MaterialTheme {
        ShareAndPostContent(
            state = ShareAndPostState(
                selectedUsers = mockSelectedUsers,
                myTeams       = mockMyTeams.map { if (it.id == "t1") it.copy(isSelected = true) else it },
                teamsImIn     = mockTeamsImIn.map { if (it.id == "t4") it.copy(isSelected = true) else it }
            ),
            onEmailQueryChange = {}, onAddUserByEmail = {}, onRemoveUser = {},
            onToggleTeam = { _, _ -> }, onConfirmShare = {}, onBackPressed = {},
            onDismissUnsavedChanges = {}, onConfirmLeave = {}
        )
    }
}