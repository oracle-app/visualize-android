package com.oracle.visualize.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date
import java.util.concurrent.TimeUnit
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.presentation.screens.shareScreen.components.MemberAvatarStackFeed

fun formatTime(date: Date, context: Context): String {
    val now = Date()
    val diff = now.time - date.time

    val mins  = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days  = TimeUnit.MILLISECONDS.toDays(diff)
    val weeks = (days / 7).toInt()

    return when {
        mins < 1   -> context.getString(R.string.time_just_now)
        mins < 60  -> context.getString(R.string.time_mins_ago, mins)
        hours < 24 -> context.getString(R.string.time_hours_ago, hours)
        days < 7   -> context.getString(R.string.time_days_ago, days)
        else       -> context.resources.getQuantityString(R.plurals.time_weeks_ago, weeks, weeks)
    }
}

/**
 * A card component used in the feed to display a visualization's summary.
 * Shows a dropdown menu with ownership-appropriate actions:
 * - Owner: Share, Delete for everyone
 * - Non-owner: Share, Delete for me
 *
 * @param item The [VisualizationCard] data to display.
 * @param currentUserID The ID of the currently authenticated user.
 * @param isMenuOpen Whether the dropdown menu is currently open.
 * @param onClick Called when the card body is tapped.
 * @param onOpenMenu Called when the MoreVert icon is tapped.
 * @param onDismissMenu Called when the menu is dismissed.
 * @param onShare Called when the user taps "Share".
 * @param onDeleteForMe Called when a non-owner taps "Delete for me".
 * @param onDeleteForEveryone Called when the owner taps "Delete for everyone".
 */
@Composable
fun FeedCard(
    item: VisualizationCard,
    currentUserID: String,
    isMenuOpen: Boolean,
    onClick: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    onDismissMenu: () -> Unit = {},
    onShare: () -> Unit = {},
    onDeleteForMe: () -> Unit = {},
    onDeleteForEveryone: () -> Unit = {}
) {
    val context = LocalContext.current
    val isOwner = item.authorID == currentUserID

    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 14.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = item.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text  = "By ${item.author}",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 13.sp
                        )
                        Text(
                            text  = "    •    ${formatTime(item.createdAt, context)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                Box {
                    IconButton(onClick = onOpenMenu) {
                        Icon(
                            imageVector        = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.feed_menu_more_options),
                            tint               = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded         = isMenuOpen,
                        onDismissRequest = onDismissMenu
                    ) {
                        DropdownMenuItem(
                            text    = {
                                Text(
                                    text  = stringResource(R.string.feed_menu_share),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = { onDismissMenu(); onShare() }
                        )

                        if (isOwner) {
                            DropdownMenuItem(
                                text    = {
                                    Text(
                                        text  = stringResource(R.string.feed_menu_delete_for_everyone),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { onDismissMenu(); onDeleteForEveryone() }
                            )
                        } else {
                            DropdownMenuItem(
                                text    = {
                                    Text(
                                        text  = stringResource(R.string.feed_menu_delete_for_me),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { onDismissMenu(); onDeleteForMe() }
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
                        .height(200.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(all = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val chart = item.chart
                    if (chart != null) {
                        ChartRenderGeneral(chart = chart)
                    } else {
                        Text(
                            text  = "Chart not found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
