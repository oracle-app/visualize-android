package com.oracle.visualize.presentation.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.presentation.screens.shareScreen.components.UserAvatarCard
import java.util.Date
import java.util.concurrent.TimeUnit

private val AVATAR_SIZE = 33.dp
private val AVATAR_OFFSET = 16.dp

fun formatTime(date: Date, context: Context): String {
    val now = Date()
    val diff = now.time - date.time

    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val weeks = (days / 7).toInt()

    return when {
        mins < 1 -> context.getString(R.string.time_just_now)
        mins < 60 -> context.getString(R.string.time_mins_ago, mins)
        hours < 24 -> context.getString(R.string.time_hours_ago, hours)
        days < 7 -> context.getString(R.string.time_days_ago, days)
        else -> context.resources.getQuantityString(
            R.plurals.time_weeks_ago,
            weeks,
            weeks
        )
    }
}

@Composable
fun FeedCard(
    item: VisualizationCard,
    currentUserID: String = "",
    isMenuOpen: Boolean = false,
    onClick: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    onDismissMenu: () -> Unit = {},
    onShare: () -> Unit = {},
    onDeleteForMe: () -> Unit = {},
    onDeleteForEveryone: () -> Unit = {}
) {
    val context = LocalContext.current
    val isAuthor = item.authorID == currentUserID

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 14.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "By ${item.author}",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "    •    ${formatTime(item.createdAt, context)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                Box {
                    IconButton(onClick = onOpenMenu) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(
                                R.string.feed_menu_more_options
                            ),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuOpen,
                        onDismissRequest = onDismissMenu
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.feed_menu_share))
                            },
                            onClick = onShare
                        )

                        if (isAuthor) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            R.string.feed_menu_delete_for_everyone
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = onDeleteForEveryone
                            )
                        } else {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            R.string.feed_menu_delete_for_me
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = onDeleteForMe
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ChartRender(chart = mockVerticalChart)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 12.dp)
                    .heightIn(min = 41.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MemberAvatarStackFeed(item.allUsersSharedWith)

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun MemberAvatarStackFeed(
    members: List<User>
) {
    val displayCount = minOf(members.size, 3)
    val extraCount = members.size - displayCount

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 14.dp)
    ) {
        Layout(
            content = {

                if (extraCount > 0) {
                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimary
                                ),
                                CircleShape
                            )
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .padding(start = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$extraCount",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                repeat(displayCount) { index ->

                    val memberIndex = displayCount - 1 - index

                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimary
                                ),
                                CircleShape
                            )
                    ) {
                        members.getOrNull(memberIndex)?.let { user ->
                            UserAvatarCard(
                                user = user,
                                size = AVATAR_SIZE.value.toInt()
                            )
                        }
                    }
                }
            }
        ) { measurables, constraints ->

            val placeables = measurables.map {
                it.measure(constraints)
            }

            val avatarSize = placeables.firstOrNull()?.width ?: 0

            val offset = (AVATAR_SIZE - AVATAR_OFFSET).roundToPx()

            val totalWidth =
                if (displayCount > 0)
                    avatarSize + (offset * (displayCount - 1))
                else 0

            val height = placeables.firstOrNull()?.height ?: 0

            layout(totalWidth, height) {

                placeables.forEachIndexed { index, placeable ->

                    val x = (displayCount - 1 - index) * offset

                    placeable.placeWithLayer(
                        x,
                        0,
                        zIndex = index.toFloat()
                    )
                }
            }
        }
    }
}

@Composable
private fun UserAvatar() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary)
    )
}
