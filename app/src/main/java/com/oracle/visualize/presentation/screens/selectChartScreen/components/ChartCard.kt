package com.oracle.visualize.presentation.screens.selectChartScreen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.ui.theme.StrongOrange

// ─── Chart colour constants ───────────────────────────────────────────────────

private val ChartBarColor       = Color(0xFF3F6FE8)  // blue bars (Units Sold)
private val ChartLineColor      = StrongOrange        // orange line (Total Transactions)
private val ChartGridColor      = Color(0xFFE0E0E0)
private val ChartAxisLabelColor = Color(0xFF9E9E9E)

// ─── Mock numeric data for chart preview ─────────────────────────────────────
// Intentionally hardcoded as placeholder values until the real configJSON
// from the chart microservice is parsed and used instead.

private val barData  = listOf(0.30f, 0.65f, 0.20f, 0.55f, 0.75f, 0.88f)
private val lineData = listOf(0.22f, 0.45f, 0.15f, 0.50f, 0.62f, 0.80f)

/**
 * Card displaying a single chart suggestion.
 *
 * When [isSelected] is true, the header and footer sections use the
 * [MaterialTheme.colorScheme.primary] background to indicate the active state.
 * Tapping anywhere on the card calls [onSelect]; the pencil icon calls [onEditTitle].
 *
 * @param visualization The visualisation data to display.
 * @param isSelected    Whether this card is currently selected.
 * @param onSelect      Called when the user taps the card to toggle selection.
 * @param onEditTitle   Called when the user taps the edit-pencil icon.
 */
@Composable
fun ChartCard(
    visualization: Visualization,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEditTitle: () -> Unit
) {
    val headerBg      = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
    val headerContent = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    val borderColor   = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Header: title + edit icon ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visualization.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = headerContent,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = onEditTitle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.chart_edit_title_description),
                        modifier = Modifier.size(18.dp),
                        tint = headerContent
                    )
                }
            }

            // ── Chart preview ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF9F9F9))
                    .padding(top = 12.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
            ) {
                DualAxisChartPreview()
            }

            // ── Footer: legend ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(
                    color      = ChartBarColor,
                    label      = stringResource(R.string.chart_legend_units_sold),
                    labelColor = headerContent
                )
                Spacer(modifier = Modifier.width(20.dp))
                LegendDot(
                    color      = ChartLineColor,
                    label      = stringResource(R.string.chart_legend_total_transaction),
                    labelColor = headerContent
                )
            }
        }
    }
}

// ─── Dual-axis bar + line chart ───────────────────────────────────────────────

/**
 * Canvas-drawn combo chart: blue bars (left y-axis) with an orange line overlay
 * (right y-axis), matching the Figma preview.
 */
@Composable
private fun DualAxisChartPreview() {
    val leftLabels  = listOf("160", "120", "80", "40", "0")
    val rightLabels = listOf(
        stringResource(R.string.chart_y_8000),
        stringResource(R.string.chart_y_6000),
        stringResource(R.string.chart_y_4000),
        stringResource(R.string.chart_y_2000),
        stringResource(R.string.chart_y_0)
    )
    val xLabels = listOf(
        stringResource(R.string.month_jan),
        stringResource(R.string.month_feb),
        stringResource(R.string.month_mar),
        stringResource(R.string.month_apr),
        stringResource(R.string.month_may),
        stringResource(R.string.month_jun)
    )

    Row(modifier = Modifier.fillMaxSize()) {

        // ── Left y-axis ───────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            leftLabels.forEach { label ->
                Text(text = label, fontSize = 7.sp, color = ChartAxisLabelColor)
            }
        }

        // ── Plot area ─────────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val plotW = size.width
                val plotH = size.height

                // Grid lines
                repeat(6) { i ->
                    val y = plotH * i / 5f
                    drawLine(
                        color       = ChartGridColor,
                        start       = Offset(0f, y),
                        end         = Offset(plotW, y),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }

                val n      = barData.size
                val groupW = plotW / n
                val barW   = groupW * 0.45f

                // Bars — Units Sold
                barData.forEachIndexed { i, h ->
                    val barH = plotH * h
                    val x    = i * groupW + (groupW - barW) / 2f
                    drawRect(
                        color   = ChartBarColor,
                        topLeft = Offset(x, plotH - barH),
                        size    = Size(barW, barH)
                    )
                }

                // Line — Total Transactions
                val linePath = Path()
                lineData.forEachIndexed { i, h ->
                    val x = i * groupW + groupW / 2f
                    val y = plotH * (1f - h)
                    if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
                }
                drawPath(
                    path  = linePath,
                    color = ChartLineColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap   = StrokeCap.Round,
                        join  = StrokeJoin.Round
                    )
                )

                // Dots on each line point
                lineData.forEachIndexed { i, h ->
                    drawCircle(
                        color  = ChartLineColor,
                        radius = 3.dp.toPx(),
                        center = Offset(i * groupW + groupW / 2f, plotH * (1f - h))
                    )
                }
            }

            // X-axis labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                xLabels.forEach { label ->
                    Text(text = label, fontSize = 7.sp, color = ChartAxisLabelColor)
                }
            }
        }

        // ── Right y-axis ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            rightLabels.forEach { label ->
                Text(text = label, fontSize = 7.sp, color = ChartLineColor)
            }
        }
    }
}

// ─── Legend dot ───────────────────────────────────────────────────────────────

@Composable
private fun LegendDot(color: Color, label: String, labelColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 9.sp, color = labelColor, fontWeight = FontWeight.Medium)
    }
}