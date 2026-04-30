package com.oracle.visualize.presentation.screens.createEditScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.screens.shareScreen.components.UserAvatar
import com.oracle.visualize.ui.theme.LightBlue
import com.oracle.visualize.ui.theme.StrongOrange
import com.oracle.visualize.ui.theme.VeryLightGray

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
                state = state,
                onEvent = viewModel::onEvent,
                onBack = onNavigateBack
            )
        }
        is CreateEditTeamUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is CreateEditTeamUiState.Success -> {
            // Handled by LaunchedEffect
        }
    }
}

@Composable
private fun CreateEditTeamContent(
    state: CreateEditTeamUiState.Content,
    onEvent: (CreateEditTeamUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = if (state.isEditMode) "Edit Your Team" else "Create New Team",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Team Name Input (Create mode only)
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
                                value = state.teamName,
                                onValueChange = { onEvent(CreateEditTeamUiEvent.NameChanged(it)) },
                                placeholder = { Text("Team's Name", color = Color.Gray) },
                                modifier = Modifier.fillMaxSize(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                        }
                    }
                }

                // Add people section
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        if (!state.isEditMode) {
                            Text(
                                text = "Add people to your team",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Search bar
                        TextField(
                            value = state.searchQuery,
                            onValueChange = { onEvent(CreateEditTeamUiEvent.SearchQueryChanged(it)) },
                            placeholder = { Text(if (state.isEditMode) "Add more people" else "Search by email", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = VeryLightGray,
                                unfocusedContainerColor = VeryLightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }

                // Search Results (if searching)
                if (state.searchQuery.isNotBlank() && state.searchResults.isNotEmpty()) {
                    items(state.searchResults) { user ->
                        SearchResultRow(user = user, onAdd = { onEvent(CreateEditTeamUiEvent.AddMember(user)) })
                    }
                }

                // Suggestions horizontal list (Create mode only, when not searching)
                if (!state.isEditMode && state.searchQuery.isBlank()) {
                    item {
                        Column {
                            Text(
                                text = "People in the same teams as you",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.suggestions) { user ->
                                    SuggestionItem(user = user, onClick = { onEvent(CreateEditTeamUiEvent.AddMember(user)) })
                                }
                            }
                        }
                    }
                }

                // Member List
                item {
                    Text(
                        text = "Member List",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }

                items(state.members) { member ->
                    MemberRow(
                        user = member,
                        isOwner = member.id == state.ownerID,
                        onRemove = { onEvent(CreateEditTeamUiEvent.RemoveMember(member)) }
                    )
                }
            }
        }

        // FAB - Checkmark
        FloatingActionButton(
            onClick = { onEvent(CreateEditTeamUiEvent.Submit) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .size(80.dp),
            containerColor = StrongOrange,
            contentColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Save team",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun SearchResultRow(user: ShareUser, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = 40)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = user.username, fontWeight = FontWeight.SemiBold)
            Text(text = user.email, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun SuggestionItem(user: ShareUser, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable { onClick() }
    ) {
        UserAvatar(user = user, size = 64)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.username,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun MemberRow(user: ShareUser, isOwner: Boolean, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = 50)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.username, fontWeight = FontWeight.Normal, fontSize = 18.sp)
            Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
        }
        if (isOwner) {
            Text(text = "owner", color = Color.Gray, fontSize = 12.sp)
        } else {
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}
