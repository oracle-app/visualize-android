package com.oracle.visualize.presentation.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.Visualization
import java.util.Date
import java.util.concurrent.TimeUnit
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.presentation.screens.shareScreen.components.MemberAvatarStackFeed

fun formatTime(date: Date, context: Context): String{
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
        else -> context.resources.getQuantityString(R.plurals.time_weeks_ago, weeks, weeks)
    }
}
@Composable
fun FeedCard(item: VisualizationCard) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(2.dp,MaterialTheme.colorScheme.outline)

    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 14.dp, end = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
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
                            text = "    •    ${formatTime(item.createdAt.toDate(), context)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("Graph", color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 12.dp)
                    .heightIn(min = 41.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MemberAvatarStackFeed(item.sharedWith)
                Spacer(modifier = Modifier.width(8.dp))
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