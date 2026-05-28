package com.oracle.visualize.presentation.screens.teamsScreen

import com.oracle.visualize.presentation.screens.teamsScreen.components.MyTeamRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.teamsScreen.components.DeleteTeamDialog
import com.oracle.visualize.presentation.screens.teamsScreen.components.TeamPosition
import com.oracle.visualize.presentation.screens.teamsScreen.components.TeamsImInRow
import com.oracle.visualize.presentation.screens.teamsScreen.components.TeamsTopBar

@Composable
fun TeamsPage(
    modifier: Modifier = Modifier,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: TeamsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onEvent(TeamsUiEvent.Refresh) }

    when (val state = uiState) {
        is TeamsUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is TeamsUiState.Content -> {
            TeamsContent(
                state    = state,
                modifier = modifier,
                onEvent  = { event ->
                    when (event) {
                        is TeamsUiEvent.NavigateToCreateTeam -> onNavigateToCreate()
                        is TeamsUiEvent.NavigateToEditTeam   -> onNavigateToEdit(event.teamId)
                        else                                 -> viewModel.onEvent(event)
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
                        Text(stringResource(R.string.teams_retry))
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TeamsTopBar()

            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text       = stringResource(R.string.teams_my_teams_section),
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color      = MaterialTheme.colorScheme.onSurface,
                        modifier   = Modifier.padding(bottom = 12.dp)
                    )
                }

                itemsIndexed(state.myTeams) { index, team ->
                    MyTeamRow(
                        team           = team,
                        isSwiped       = state.swipedTeamId == team.id,
                        position       = when {
                            state.myTeams.size == 1         -> TeamPosition.SINGLE
                            index == 0                      -> TeamPosition.TOP
                            index == state.myTeams.size - 1 -> TeamPosition.BOTTOM
                            else                            -> TeamPosition.MIDDLE
                        },
                        onSwipe        = { onEvent(TeamsUiEvent.SwipeTeam(team.id)) },
                        onDismissSwipe = { onEvent(TeamsUiEvent.SwipeTeam(null)) },
                        onEdit         = { onEvent(TeamsUiEvent.NavigateToEditTeam(team.id)) },
                        onDelete       = { onEvent(TeamsUiEvent.RequestDeleteTeam(team.id)) }
                    )
                    if (index < state.myTeams.size - 1) Spacer(modifier = Modifier.height(3.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text       = stringResource(R.string.teams_im_in_section),
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color      = MaterialTheme.colorScheme.onSurface,
                        modifier   = Modifier.padding(bottom = 12.dp)
                    )
                }

                itemsIndexed(state.teamsImIn) { index, team ->
                    TeamsImInRow(
                        team       = team,
                        isExpanded = team.id in state.expandedTeamIds,
                        position   = when {
                            state.teamsImIn.size == 1         -> TeamPosition.SINGLE
                            index == 0                        -> TeamPosition.TOP
                            index == state.teamsImIn.size - 1 -> TeamPosition.BOTTOM
                            else                              -> TeamPosition.MIDDLE
                        },
                        onToggle   = { onEvent(TeamsUiEvent.ToggleExpand(team.id)) }
                    )
                    if (index < state.teamsImIn.size - 1) Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }

        FloatingActionButton(
            onClick        = { onEvent(TeamsUiEvent.NavigateToCreateTeam) },
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp)
                .size(80.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor   = MaterialTheme.colorScheme.onSecondary,
            shape          = RoundedCornerShape(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.teams_create_fab_description), modifier = Modifier.size(40.dp))
        }

        if (state.teamPendingDeleteId != null) {
            DeleteTeamDialog(
                onConfirm = { onEvent(TeamsUiEvent.ConfirmDeleteTeam(state.teamPendingDeleteId)) },
                onDismiss = { onEvent(TeamsUiEvent.DismissDeleteDialog) }
            )
        }
    }
}
