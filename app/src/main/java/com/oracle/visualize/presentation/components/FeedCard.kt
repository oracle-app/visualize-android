package com.oracle.visualize.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.policyObjects.VisualizationPermissions
import com.oracle.visualize.presentation.screens.feedScreen.components.FeedCardMenu
import com.oracle.visualize.presentation.screens.feedScreen.components.MemberAvatarStackFeed
import com.oracle.visualize.presentation.screens.feedScreen.components.skeletonEffect
import com.oracle.visualize.ui.theme.ChartPalette
import java.util.Date
import java.util.concurrent.TimeUnit

fun formatTime(date: Date, context: Context): String {
    val now   = Date()
    val diff  = now.time - date.time
    val mins  = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days  = TimeUnit.MILLISECONDS.toDays(diff)
    val weeks = (days / 7).toInt()
    return when {
        mins  < 1  -> context.getString(R.string.time_just_now)
        mins  < 60 -> context.getString(R.string.time_mins_ago, mins)
        hours < 24 -> context.getString(R.string.time_hours_ago, hours)
        days  < 7  -> context.getString(R.string.time_days_ago, days)
        else       -> context.resources.getQuantityString(R.plurals.time_weeks_ago, weeks, weeks)
    }
}

@Composable
fun FeedCard(
    item: VisualizationCard,
    currentUserID: String = "",
    onClick: () -> Unit = {},
    chart: Chart<*>?,
    chartColorTheme: ChartPalette = ChartPalette.THEME1,
    isChartLoading: Boolean,
    onLoadChartRequest: () -> Unit,
    permissions: VisualizationPermissions,
    isDeletable: Boolean = false,
    isMenuOpen: Boolean = false,
    onMenuOpen: () -> Unit = {},
    onMenuDismiss: () -> Unit = {},
    onDeleteForEveryone: () -> Unit = {},
    onHideForMe: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    val context        = LocalContext.current
    val isShared       = item.allUsersSharedWith.isNotEmpty()
    var titleLineCount by remember { mutableStateOf(1) }
    val amIAuthor      = item.authorID == currentUserID
    val _chartHeight   = if (isShared) 200.dp else 248.dp
    val chartHeight    = maxOf(100.dp, _chartHeight - (22.dp * (titleLineCount - 1)))

    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 14.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text         = item.title,
                        fontWeight   = FontWeight.SemiBold,
                        fontSize     = 16.sp,
                        color        = MaterialTheme.colorScheme.onPrimaryContainer,
                        onTextLayout = { titleLineCount = it.lineCount }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (amIAuthor) stringResource(R.string.by_me)
                            else stringResource(R.string.by_author, item.author),
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Text(text = "...", color = MaterialTheme.colorScheme.surfaceVariant)
                        Text(
                            text  = stringResource(R.string.bullet_separator),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "...", color = MaterialTheme.colorScheme.surfaceVariant)
                        Text(
                            text     = formatTime(item.createdAt, context),
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    IconButton(onClick = onMenuOpen) {
                        Icon(
                            imageVector        = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.icon_menu),
                            tint               = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (isMenuOpen) {
                        val density = LocalDensity.current
                        Popup(
                            alignment        = Alignment.TopEnd,
                            onDismissRequest = onMenuDismiss,
                            offset           = with(density) { IntOffset(0, 44.dp.roundToPx()) },
                            properties       = PopupProperties(focusable = true)
                        ) {
                            FeedCardMenu(
                                canDelete           = permissions.canDelete,
                                canHide             = permissions.canHide,
                                canShare            = permissions.canShare,
                                onDismiss           = onMenuDismiss,
                                onShare             = onShare,
                                onDeleteForEveryone = onDeleteForEveryone,
                                onHideForMe         = onHideForMe
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(12.dp))
                        .padding(all = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LaunchedEffect(item.id) {
                        if (isChartLoading && chart == null) onLoadChartRequest()
                    }
                    if (isChartLoading) {
                        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).skeletonEffect())
                    } else if (chart != null) {
                        ChartRenderGeneral(
                            modifier         = Modifier.fillMaxSize(),
                            chart            = chart,
                            showAxisLabels   = false,
                            enableTooltips   = false,
                            enableZoomAndPan = false,
                            feedCardLabels   = true,
                            chartColorTheme = chartColorTheme
                        )
                    } else {
                        Text(
                            text  = stringResource(R.string.error_chart_not_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (isShared) {
                Row(
                    modifier          = Modifier.padding(start = 12.dp, bottom = 12.dp).heightIn(min = 41.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MemberAvatarStackFeed(item.allUsersSharedWith)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            } else {
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}
