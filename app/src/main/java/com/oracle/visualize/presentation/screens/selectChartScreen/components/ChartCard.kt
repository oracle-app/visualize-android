package com.oracle.visualize.presentation.screens.selectChartScreen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.ui.theme.*

@Composable
fun ChartCard(
    visualization: Visualization,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEditTitle: () -> Unit
) {
    val topBottomBackground = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    val iconColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBottomBackground)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visualization.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = onEditTitle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Title",
                        modifier = Modifier.size(20.dp),
                        tint = iconColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF9F9F9))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                MockChartContent()
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBottomBackground)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = Color.Blue, text = "Units Sold", textColor = contentColor)
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = Color.Yellow, text = "Total Transactions", textColor = contentColor)
            }
        }
    }
}

@Composable
fun MockChartContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("20,000", "15,000", "10,000", "5,000", "0").forEach { label ->
                    Text(text = label, fontSize = 8.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 35.dp, bottom = 20.dp, end = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(5) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = Color.LightGray.copy(alpha = 0.4f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val barHeights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 0.4f, 0.5f)
                    barHeights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(h)
                                .width(10.dp)
                                .background(Color.Blue)
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val orangeLine = Color.Yellow
                    val points = listOf(0.7f, 0.5f, 0.8f, 0.4f, 0.7f, 0.3f, 0.6f, 0.4f)
                    val path = Path()
                    
                    val stepX = size.width / (points.size - 1)
                    
                    points.forEachIndexed { index, h ->
                        val x = index * stepX
                        val y = size.height * (1f - h)
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    
                    drawPath(
                        path = path,
                        color = orangeLine,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(start = 35.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug").forEach { month ->
                    Text(text = month, fontSize = 8.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 9.sp, color = textColor)
    }
}
