package com.oracle.visualize.ui.screens.chartselection.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.data.model.Chart
import com.oracle.visualize.data.model.ChartType

/**
 * Card component to display a chart preview and its title.
 */
@Composable
fun ChartCard(
    chart: Chart,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (chart.isSelected) Color(0xFF2E7D32) else Color.Transparent
    val backgroundColor = if (chart.isSelected) Color(0xFFE0F2F1) else Color.White

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = chart.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF37474F)
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit title",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                MockChartCanvas(chart)
            }
        }
    }
}

/**
 * Draws a mock representation of the chart data.
 */
@Composable
private fun MockChartCanvas(chart: Chart) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val barWidth = width / (chart.data.size * 2f)
        val maxVal = chart.data.maxOrNull() ?: 100f

        chart.data.forEachIndexed { index, value ->
            val x = index * (barWidth * 2) + barWidth / 2
            val barHeight = (value / maxVal) * height

            // Draw Bar for BAR or COMBINED types
            if (chart.type == ChartType.BAR || chart.type == ChartType.COMBINED) {
                drawRect(
                    color = Color(0xFF1976D2),
                    topLeft = Offset(x, height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }

            // Draw Line for LINE or COMBINED types
            if (chart.type == ChartType.LINE || chart.type == ChartType.COMBINED) {
                if (index < chart.data.size - 1) {
                    val nextValue = chart.data[index + 1]
                    val nextX = (index + 1) * (barWidth * 2) + barWidth / 2
                    val nextBarHeight = (nextValue / maxVal) * height

                    drawLine(
                        color = Color(0xFFF4511E),
                        start = Offset(x + barWidth / 2, height - barHeight),
                        end = Offset(nextX + barWidth / 2, height - nextBarHeight),
                        strokeWidth = 3f
                    )
                }
            }
        }
    }
}
