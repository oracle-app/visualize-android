package com.oracle.visualize.presentation.screens.notificationScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.models.NotificationSection

/**
 * Main Composable for the Notifications screen.
 * Displays a list of notifications grouped by date sections.
 *
 * @param modifier Modifier for the layout.
 * @param viewModel The [NotificationViewModel] providing data and logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPage(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFD9E9E9)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is NotificationUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NotificationUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is NotificationUiState.Success -> {
                    if (state.notifications.isEmpty()) {
                        EmptyNotificationsState()
                    } else {
                        NotificationsList(state.notifications, viewModel)
                    }
                }
            }
        }
    }
}

/**
 * Renders the list of notifications grouped by [NotificationSection].
 */
@Composable
private fun NotificationsList(
    notifications: List<Notification>,
    viewModel: NotificationViewModel
) {
    val grouped = notifications.groupBy { viewModel.getSection(it.createdAt) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NotificationSection.entries.forEach { section ->
            val sectionNotifications = grouped[section] ?: emptyList()
            
            item {
                Text(
                    text = when (section) {
                        NotificationSection.TODAY -> "Today"
                        NotificationSection.YESTERDAY -> "Yesterday"
                        NotificationSection.LAST_30_DAYS -> "Last 30 days"
                    },
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (sectionNotifications.isEmpty()) {
                item {
                    Text(
                        text = "No notifications yet",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(sectionNotifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        timestamp = viewModel.formatTimestamp(notification.createdAt)
                    )
                }
            }
        }
        
        item {
            Text(
                text = "No more notifications",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * UI component representing a single notification item.
 */
@Composable
private fun NotificationItem(notification: Notification, timestamp: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F4F4)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = notification.avatarRes),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = timestamp,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * UI displayed when there are no notifications.
 */
@Composable
private fun EmptyNotificationsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Notifications Yet",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF34797C),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We'll notify you when there's something new.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}
