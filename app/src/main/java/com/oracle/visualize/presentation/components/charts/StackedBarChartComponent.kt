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
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.StackedVerticalBarPlot
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Renders a stacked bar chart based on the provided [StackedBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderStackedBarChart(
    modifier: Modifier = Modifier, chart: StackedBarChart, showAxisLabels: Boolean,
    enableTooltips: Boolean
) {
    val categories = chart.data.keys.toList()
    val seriesCount = chart.data.values.firstOrNull()?.size ?: 0
    val seriesNames = if (chart.stackNames.size >= seriesCount) {
        chart.stackNames
    } else {
        List(seriesCount) { i -> "Series ${i + 1}" }
    }
    val seriesColors = generateChartColors(seriesNames.size)
    val maxY = chart.data.values.maxOfOrNull { it.sum() } ?: 0f

    Box {
        KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Gray, minorGridlineStyle = null)) {
            XYGraph(
                xAxisModel = remember { CategoryAxisModel(categories) },
                yAxisModel = rememberFloatLinearAxisModel(0f..maxOf(1f, maxY), minorTickCount = 0),
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
                    title = {
                        if (showAxisLabels && !chart.metrics.isEmpty()) {
                            Text(chart.metrics[0], style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                        }
                    }
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
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
                )
            ) {
                StackedVerticalBarPlot {
                    if (categories.isNotEmpty() && seriesCount > 0) {
                        seriesNames.forEachIndexed { seriesIndex, _ ->
                            series {
                                chart.data.forEach { (category, values) ->
                                    if (seriesIndex < values.size) {
                                        item(
                                            x = category,
                                            y = values[seriesIndex],
                                            bar = { _, itemIndex, plotEntry ->
                                                val tooltipDisplayState = rememberTooltipState(
                                                    initialIsVisible = false, isPersistent = true
                                                )

                                                if (!enableTooltips && tooltipDisplayState.isVisible) {
                                                    tooltipDisplayState.dismiss()
                                                }

                                                val currentYValue = plotEntry.y.end - plotEntry.y.start

                                                TooltipBox(
                                                    tooltip = { PlainTooltip { Text(text = "${seriesNames[itemIndex]}: $currentYValue") } },
                                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                                        positioning = TooltipAnchorPosition.Above,
                                                    ),
                                                    state = tooltipDisplayState
                                                ) {
                                                    DefaultBar(
                                                        brush = SolidColor(seriesColors[itemIndex]),
                                                        modifier = modifier.fillMaxWidth()
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
