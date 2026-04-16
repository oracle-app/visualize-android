package com.oracle.visualize.presentation.screens.ChartSelection.components

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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) TealPrimary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
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
                    text = visualization.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = onEditTitle,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Title",
                        modifier = Modifier.size(16.dp),
                        tint = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F9F9))
                    .border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                MockChartContent()
            }
        }
    }
}

@Composable
fun MockChartContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            // Y-Axis Labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("20,000", "15,000", "10,000", "5,000", "0").forEach { label ->
                    Text(text = label, fontSize = 8.sp, color = TextGray)
                }
            }

            // Chart Drawing Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 35.dp, bottom = 20.dp, end = 8.dp)
            ) {
                // Grid Lines
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

                // Bars
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
                                .background(ChartBarBlue)
                        )
                    }
                }

                // Trend Line (Orange)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val orangeLine = ChartLineOrange
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
            
            // X-Axis Labels (Months)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(start = 35.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug").forEach { month ->
                    Text(text = month, fontSize = 8.sp, color = TextGray)
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = ChartBarBlue, text = "Units Sold")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(color = ChartLineOrange, text = "Total Transactions")
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 9.sp, color = TextDark)
    }
}
