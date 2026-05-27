package com.oracle.visualize.presentation.screens.threadsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.UserAvatar
import com.oracle.visualize.presentation.screens.threadsScreen.CommentUiModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentCard(
    comment: CommentUiModel,
    currentUserId: String,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteThreadClick: (threadId: String) -> Unit
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

    val formattedDate = SimpleDateFormat(
        "dd/MM/yy",
        Locale.getDefault()
    ).format(comment.createdAt)

    var expanded by remember { mutableStateOf(false) }

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
            verticalAlignment = Alignment.CenterVertically
        ) {

            UserAvatar(
                username = comment.authorName,
                profilePictureURL = comment.authorImageURL,
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
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (isCurrentUser) {
                Box {
                    IconButton(
                        onClick = {
                            expanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.more),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.reply)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Reply,
                                    contentDescription = stringResource(R.string.reply),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                            onClick = {
                                expanded = false
                                onReplyClick()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.delete),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                expanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = onReplyClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = stringResource(R.string.reply),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )
        if (!comment.imageURL.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = comment.imageURL,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 14.dp, bottom = 12.dp)
                    .width(155.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
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
                    ThreadCard(
                        thread = thread,
                        isCurrentUser = thread.authorID == currentUserId,
                        onDeleteClick = {
                            onDeleteThreadClick(thread.id)
                        }
                    )
                }
            }
        }
    }
}
