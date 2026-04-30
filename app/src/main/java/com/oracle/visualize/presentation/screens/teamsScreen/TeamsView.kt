package com.oracle.visualize.presentation.screens.teamsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.shareScreen.components.MemberAvatarStack
import com.oracle.visualize.presentation.screens.shareScreen.components.UserAvatar
import com.oracle.visualize.ui.theme.LightBlue
import com.oracle.visualize.ui.theme.LighterBlue
import com.oracle.visualize.ui.theme.StrongOrange

@Composable
fun TeamsPage(
    modifier: Modifier = Modifier,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: TeamsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Refresh data when navigating back to this screen
    LaunchedEffect(Unit) {
        viewModel.onEvent(TeamsUiEvent.Refresh)
    }

    when (val state = uiState) {
        is TeamsUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is TeamsUiState.Content -> {
            TeamsContent(
                state = state,
                modifier = modifier,
                onEvent = { event ->
                    when (event) {
                        is TeamsUiEvent.NavigateToCreateTeam -> onNavigateToCreate()
                        is TeamsUiEvent.NavigateToEditTeam   -> onNavigateToEdit(event.teamId)
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }

        is TeamsUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.onEvent(TeamsUiEvent.Refresh) }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamsContent(
    state: TeamsUiState.Content,
    modifier: Modifier = Modifier,
    onEvent: (TeamsUiEvent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue)
                    .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Teams",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "My Teams",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                itemsIndexed(state.myTeams) { index, team ->
                    MyTeamRow(
                        team = team,
                        isSwiped = state.swipedTeamId == team.id,
                        position = when {
                            state.myTeams.size == 1 -> TeamPosition.SINGLE
                            index == 0              -> TeamPosition.TOP
                            index == state.myTeams.size - 1 -> TeamPosition.BOTTOM
                            else                    -> TeamPosition.MIDDLE
                        },
                        onSwipe = { onEvent(TeamsUiEvent.SwipeTeam(team.id)) },
                        onDismissSwipe = { onEvent(TeamsUiEvent.SwipeTeam(null)) },
                        onEdit = { onEvent(TeamsUiEvent.NavigateToEditTeam(team.id)) },
                        onDelete = { onEvent(TeamsUiEvent.DeleteTeam(team.id)) }
                    )
                    if (index < state.myTeams.size - 1) {
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                item {
                    Text(
                        text = "Teams I'm In",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                itemsIndexed(state.teamsImIn) { index, team ->
                    TeamsImInRow(
                        team = team,
                        isExpanded = team.id in state.expandedTeamIds,
                        position = when {
                            state.teamsImIn.size == 1 -> TeamPosition.SINGLE
                            index == 0                -> TeamPosition.TOP
                            index == state.teamsImIn.size - 1 -> TeamPosition.BOTTOM
                            else                      -> TeamPosition.MIDDLE
                        },
                        onToggle = { onEvent(TeamsUiEvent.ToggleExpand(team.id)) }
                    )
                    if (index < state.teamsImIn.size - 1) {
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onEvent(TeamsUiEvent.NavigateToCreateTeam) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .size(80.dp),
            containerColor = StrongOrange,
            contentColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create team",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

enum class TeamPosition { SINGLE, TOP, MIDDLE, BOTTOM }

@Composable
private fun MyTeamRow(
    team: ShareTeam,
    isSwiped: Boolean,
    position: TeamPosition,
    onSwipe: () -> Unit,
    onDismissSwipe: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val shape = when (position) {
        TeamPosition.TOP    -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        TeamPosition.MIDDLE -> RoundedCornerShape(4.dp)
        TeamPosition.BOTTOM -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
        TeamPosition.SINGLE -> RoundedCornerShape(16.dp)
    }

    val offset by animateDpAsState(targetValue = if (isSwiped) (-140).dp else 0.dp, label = "swipeOffset")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(LighterBlue)
    ) {
        // Actions at the back (right)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(140.dp)
                .fillMaxHeight()
        ) {
            // Edit Button (Teal)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF34797C))
                    .clickable { onEdit(); onDismissSwipe() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            // Delete Button (Red)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFEC4848))
                    .clickable { onDelete(); onDismissSwipe() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Main card content (front, with offset)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset(x = offset)
                .fillMaxWidth()
                .background(LighterBlue)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -15f) onSwipe()
                        if (dragAmount > 15f) onDismissSwipe()
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = team.name, fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${team.memberCount} members", fontSize = 14.sp, color = Color.Gray)
            }
            MemberAvatarStack(members = team.members, isSelected = false)
        }
    }
}

@Composable
private fun TeamsImInRow(
    team: ShareTeam,
    isExpanded: Boolean,
    position: TeamPosition,
    onToggle: () -> Unit
) {
    val shape = when (position) {
        TeamPosition.TOP    -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        TeamPosition.MIDDLE -> RoundedCornerShape(4.dp)
        TeamPosition.BOTTOM -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
        TeamPosition.SINGLE -> RoundedCornerShape(16.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(LighterBlue)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { onToggle() }
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = team.name, fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${team.memberCount} members", fontSize = 14.sp, color = Color.Gray)
            }
            MemberAvatarStack(members = team.members, isSelected = false)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Black
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                team.members.forEach { member ->
                    MemberListItem(user = member, isOwner = member.id == "user1")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MemberListItem(user: ShareUser, isOwner: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserAvatar(user = user, size = 40)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.username, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(text = user.email, fontSize = 12.sp, color = Color.Gray)
        }
        if (isOwner) {
            Text(text = "owner", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
