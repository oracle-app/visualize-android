package com.oracle.visualize.presentation.components.charts


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.TileChart
import com.oracle.visualize.ui.theme.ChartPalette

/**
 * Renders a Tile Chart component.
 * Layout strategy from 1 to 9 items based on user requirement.
 */
@Composable
fun RenderTileChart(
    modifier: Modifier = Modifier,
    chart: TileChart,
    chartColorTheme: ChartPalette,
    isFeedCard: Boolean = false
) {
    val data = chart.data
    val entries = data.entries.toList()
    val maxTiles = if (isFeedCard) 6 else 9
    val count = entries.size.coerceAtMost(maxTiles)
    val colors = chartColorTheme.colors

    // Calculate row distribution logic
    val rowCounts = when (count) {
        3 -> listOf(2, 1)
        4 -> listOf(2, 2)
        else -> {
            val rows = mutableListOf<Int>()
            var remaining = count
            while (remaining > 3) {
                rows.add(3)
                remaining -= 3
            }
            if (remaining > 0) rows.add(remaining)
            rows
        }
    }

    val scaleFactor = if (isFeedCard && rowCounts.size > 1) 0.65f else 1.0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (isFeedCard) 4.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var currentEntryIndex = 0
        rowCounts.forEach { rowSize ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp)
            ) {
                repeat(rowSize) {
                    val entry = entries[currentEntryIndex]
                    TileItem(
                        modifier = Modifier.weight(1f),
                        title = entry.key,
                        value = entry.value,
                        borderColor = colors[currentEntryIndex % colors.size],
                        scaleFactor = scaleFactor
                    )
                    currentEntryIndex++
                }
            }
        }
    }
}

/**
 * Individual Tile Item rendering logic.
 */
@Composable
private fun TileItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    borderColor: Color,
    scaleFactor: Float = 1.0f
) {
    Column(
        modifier = modifier
            .border(1.dp, borderColor)
            .padding(
                horizontal = (12 * scaleFactor).dp,
                vertical = (8 * scaleFactor).dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = (15 * scaleFactor).sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = (24 * scaleFactor).sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
