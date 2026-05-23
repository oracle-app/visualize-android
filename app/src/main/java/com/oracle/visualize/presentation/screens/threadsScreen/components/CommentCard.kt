@file:JvmName("CommentCardKt")

package com.oracle.visualize.presentation.screens.threadsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.presentation.components.UserAvatar

@Composable
fun CommentCard(
    comment: Comment,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onTertiary
    }

    val headerColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val timelineColor = MaterialTheme.colorScheme.tertiaryFixed

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerColor)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {

            UserAvatar(
                username = comment.authorName,
                profilePictureURL = comment.authorImageUrl,
                size = 38
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = comment.createdAt.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Reply,
                contentDescription = stringResource(R.string.reply),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 24.dp, end = 14.dp, bottom = 14.dp)
                .drawBehind {
                    val avatarColumnWidth = 38.dp.toPx()
                    val lineX = avatarColumnWidth / 2f

                    drawLine(
                        color = timelineColor,
                        start = Offset(lineX, 0f),
                        end = Offset(lineX, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                }
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                comment.threads.forEach { thread ->
                    ThreadCard(thread = thread)
                }
            }
        }
    }
}
