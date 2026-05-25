package com.oracle.visualize.presentation.screens.threadsScreen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oracle.visualize.presentation.components.UserAvatar
import com.oracle.visualize.presentation.screens.threadsScreen.ThreadUiModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ThreadCard(
    thread: ThreadUiModel,
    modifier: Modifier = Modifier
) {
    val formattedDate = SimpleDateFormat(
        "dd/MM/yy",
        Locale.getDefault()
    ).format(thread.createdAt)

    Row(
        modifier = modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier.width(38.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            UserAvatar(
                username = thread.authorName,
                profilePictureURL = thread.authorImageURL,
                size = 32,
                modifier = Modifier.border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.tertiaryFixed,
                    shape = CircleShape
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(
                topStart = 2.dp,
                topEnd = 14.dp,
                bottomStart = 14.dp,
                bottomEnd = 14.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryFixed
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = thread.authorName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = thread.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
