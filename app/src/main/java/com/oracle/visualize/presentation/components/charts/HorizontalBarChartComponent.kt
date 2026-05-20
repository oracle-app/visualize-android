package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.HorizontalBarPlot
import io.github.koalaplot.core.gestures.GestureConfig
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel

/**
 * Renders a horizontal bar chart based on the provided [HorizontalBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderHorizontalBarChart(
    modifier: Modifier = Modifier,
    chart: HorizontalBarChart,
    showAxisLabels: Boolean
) {
    val categories = chart.data.keys.toList()
    val values = chart.data.values.toList()
    val maxValue = values.maxOrNull() ?: 0f
    val barColors = generateChartColors(categories.size)

    Box {
        KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Gray, minorGridlineStyle = null)) {
            XYGraph(
                xAxisModel = rememberFloatLinearAxisModel(
                    range = 0f..maxValue,
                    minorTickCount = 0
                ),
                yAxisModel = remember { CategoryAxisModel(categories) },
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
                    title = {
                        if (showAxisLabels && !chart.metrics.isEmpty()) {
                            Text(chart.metrics[0], style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                        }
                    }
                ) ,
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
                    title = {
                        if (showAxisLabels && !chart.metrics.isEmpty()) {
                            Box(modifier = modifier.width(25.dp).height(1.dp).rotate(90f)) {
                                Text(
                                    text = chart.metrics[1],
                                    overflow = TextOverflow.Visible,
                                    softWrap = false,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                ),
                gestureConfig = GestureConfig(
                    panXEnabled = true,
                    panYEnabled = true
                )
            ) {
                HorizontalBarPlot(
                    xData = values,
                    yData = categories,
                    bar = { index, _, _ ->
                        val tooltipDisplayState = rememberTooltipState(
                            initialIsVisible = false, isPersistent = true
                        )

                        TooltipBox(
                            tooltip = { PlainTooltip { Text(text = "${categories[index]}: ${values[index]}") } },
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                positioning = TooltipAnchorPosition.Above,
                            ),
                            state = tooltipDisplayState
                        ) {
                            DefaultBar(
                                brush = SolidColor(barColors[index]),
                                modifier = modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    }
}
