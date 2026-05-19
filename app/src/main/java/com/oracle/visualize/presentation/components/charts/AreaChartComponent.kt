package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.common.collect.Multimaps.index
import com.oracle.visualize.domain.models.AreaChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.line.StackedAreaPlot
import io.github.koalaplot.core.line.StackedAreaPlotDataAdapter
import io.github.koalaplot.core.line.StackedAreaStyle
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlin.collections.getOrNull

/**
 * Renders an area chart based on the provided [AreaChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderAreaChart(
    modifier: Modifier = Modifier,
    chart: AreaChart,
    showAxisLabels: Boolean
) {
    val sortedKeys = chart.data.keys.sorted()
    val minX = sortedKeys.firstOrNull() ?: 0f
    val maxX = sortedKeys.lastOrNull() ?: 0f
    val maxY = chart.data.values.maxOfOrNull { it.sum() } ?: 0f

    val seriesNames = chart.stackNames
    val seriesColors = generateChartColors(seriesNames.size)

    val seriesData = List(seriesNames.size) { seriesIndex ->
        sortedKeys.map { x -> chart.data[x]?.getOrNull(seriesIndex) ?: 0f }
    }

    val areaProcessedData = StackedAreaPlotDataAdapter(xData = sortedKeys, yData = seriesData)
    val lineProcessedData = chart.data.map { (x, y) ->

    }

    KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Gray, minorGridlineStyle = null)) {
        XYGraph(
            xAxisModel = rememberFloatLinearAxisModel(minX..maxOf(minX + 1f, maxX)),
            yAxisModel = rememberFloatLinearAxisModel(0f..maxOf(1f, maxY)),
            xAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
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
            StackedAreaPlot(
                data = areaProcessedData,
                styles = seriesColors.map { col ->
                    StackedAreaStyle(
                        lineStyle = LineStyle(brush = SolidColor(col), strokeWidth = 2.dp ),
                        areaStyle = AreaStyle(brush = SolidColor(col.copy(alpha = 0.2f))),
                    )
                },
                firstBaseline = AreaBaseline.HorizontalLine(0f),
            )
        }
    }
}
