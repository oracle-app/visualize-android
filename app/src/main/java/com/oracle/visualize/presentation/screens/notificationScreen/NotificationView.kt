package com.oracle.visualize.presentation.screens.notificationScreen



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.screens.notificationScreen.components.NotificationCard
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.enums.NotificationGroup

/**
 * Placeholder screen for notifications.
 */
@Composable
fun NotificationPage(modifier: Modifier = Modifier,
                     viewModel: NotificationViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) {
        paddingValues ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = stringResource(uiState.error!!),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {

                    NotificationGroup.entries.forEach { group ->
                        val notifications = uiState.groupedNotifications[group]

                        val subtitle = when (group) {
                            NotificationGroup.TODAY      -> R.string.today
                            NotificationGroup.YESTERDAY  -> R.string.yesterday
                            NotificationGroup.LAST30     -> R.string.last_30_days
                            NotificationGroup.OLDER      -> R.string.older
                        }

                        item{
                            Text(
                                text = stringResource(subtitle),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        if (notifications.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_notifications_yet),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }else {
                            items(items = notifications, key = { it.id }) { notification ->
                                NotificationCard(notification = notification)
                            }                        }

                    }

                }
            }
        }
    }
}


