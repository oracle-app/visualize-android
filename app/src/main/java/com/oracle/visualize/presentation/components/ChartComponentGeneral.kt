package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.AreaChart
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.DonutChart
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.domain.models.PieChartModel
import com.oracle.visualize.domain.models.ScatterChart
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.domain.models.VerticalBarChart
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.HorizontalBarPlot
import io.github.koalaplot.core.bar.StackedVerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.bar.verticalSolidBar
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.line.StackedAreaPlot
import io.github.koalaplot.core.line.StackedAreaPlotDataAdapter
import io.github.koalaplot.core.line.StackedAreaStyle
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlin.collections.sum
import kotlin.math.roundToInt

/**
 * Renders a chart based on the provided [Chart] data using KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ChartRenderGeneral(chart: Chart<*>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
        .padding(8.dp)) {
        when (chart) {
            is VerticalBarChart -> {
                RenderVerticalBarChart(chart, modifier)
            }

            is HorizontalBarChart -> {
                RenderHorizontalBarChart(chart, modifier)
            }

            is StackedBarChart -> {
                RenderStackedBarChart(chart)
            }

            is LineChart -> {
                RenderLineChart(chart)
            }

            is ScatterChart -> {
                RenderScatterChart(chart)
            }

            is PieChartModel -> {
                RenderPieChart(chart, modifier)
            }

            is DonutChart -> {
                RenderDonutChart(chart, modifier)
            }

            is AreaChart -> {
                RenderAreaChart(chart)
            }
        }
    }
}


/** Generates a random color. Will later be replaced by user's theme preference.
 *
 * @param n The amount of colors to be created.
 * @returns a list of [Color] objects.
 * */
fun generateChartColors(n: Int): List<Color> {
    if (n <= 0) return emptyList()
    return List(n) { i ->
        Color.hsv(i * 360f / n, 0.6f, 0.9f)
    }
}


/**
 * Renders a vertical bar chart based on the provided [VerticalBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@Composable
private fun RenderVerticalBarChart(chart: VerticalBarChart, modifier: Modifier = Modifier) {
    val categories = chart.data.keys.toList()
    val values = chart.data.values.toList()
    val maxValue = values.maxOrNull() ?: 0f
    val barColors = generateChartColors(categories.size)

    Box {
        XYGraph(
            xAxisModel = remember { CategoryAxisModel(categories) },
            yAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
            xAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {}
            ),
            yAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {}
            )
        ) {
            VerticalBarPlot(
                xData = categories,
                yData = values,
                bar = { index, _, _ ->
                    DefaultBar(
                        brush = SolidColor(barColors[index]),
                        modifier = modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}


/**
 * Renders a horizontal bar chart based on the provided [HorizontalBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@Composable
private fun RenderHorizontalBarChart(chart: HorizontalBarChart, modifier: Modifier = Modifier) {
    val categories = chart.data.keys.toList()
    val values = chart.data.values.toList()
    val maxValue = values.maxOrNull() ?: 0f
    val barColors = generateChartColors(categories.size)

    Box {
        XYGraph(
            xAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
            yAxisModel = remember { CategoryAxisModel(categories) },
            xAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {}
            ),
            yAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {}
            )
        ) {
            HorizontalBarPlot(
                xData = values,
                yData = categories,
                bar = { index, _, _ ->
                    DefaultBar(
                        brush = SolidColor(barColors[index]),
                        modifier = modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}


/**
 * Renders a stacked bar chart based on the provided [StackedBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@Composable
private fun RenderStackedBarChart(chart: StackedBarChart) {
    val categories = chart.data.keys.toList()
    val seriesCount = chart.data.values.firstOrNull()?.size ?: 0
    val seriesNames = if (chart.stackNames.size >= seriesCount) {
        chart.stackNames
    } else {
        List(seriesCount) { i -> "Series ${i + 1}" }
    }
    val seriesColors = generateChartColors(seriesNames.size)
    val maxY = chart.data.values.maxOfOrNull { it.sum() } ?: 0f

    if (categories.isNotEmpty() && seriesCount > 0) {
        XYGraph(
            xAxisModel = remember { CategoryAxisModel(categories) },
            yAxisModel = rememberFloatLinearAxisModel(0f..maxOf(1f, maxY), minorTickCount = 0),
            xAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {}
            ),
            yAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {}
            )
        ) {
            StackedVerticalBarPlot {
                seriesNames.forEachIndexed { seriesIndex, _ ->
                    series(defaultBar = verticalSolidBar(seriesColors[seriesIndex], RectangleShape, border = null)) {
                        chart.data.forEach { (category, values) ->
                            if (seriesIndex < values.size) {
                                item(category, values[seriesIndex])
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Renders a line chart based on the provided [LineChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@Composable
private fun RenderLineChart(chart: LineChart) {
    val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
    val lineColor = generateChartColors(1).firstOrNull() ?: Color.Blue

    XYGraph (
        xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
        yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
        xAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {}
        ),
        yAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {}
        )
    ) {
        LinePlot(
            data = processedData,
            lineStyle = LineStyle(SolidColor(lineColor), strokeWidth = 2.dp)
        )
    }
}


/**
 * Renders a scatter plot chart based on the provided [ScatterChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun RenderScatterChart(chart: ScatterChart) {
    val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
    val dotColor = generateChartColors(1).firstOrNull() ?: Color.Blue

    XYGraph (
        xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
        yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
        xAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {}
        ),
        yAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {}
        )
    ) {
        LinePlot(
            data = processedData,
            symbol = {
                Symbol(fillBrush = SolidColor(dotColor), outlineBrush = SolidColor(dotColor))
            }
        )
    }
}


/**
 * Renders a pie chart based on the provided [PieChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun RenderPieChart(chart: PieChartModel, modifier: Modifier = Modifier) {
    val categories = chart.fieldNames
    val values = chart.data
    val colors = generateChartColors(categories.size)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(
            values = values,
            label = { index ->
                Text(
                    text = values[index].toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            slice = { index -> DefaultSlice(color = colors[index]) }
        )
    }
}


/**
 * Renders a donut chart based on the provided [DonutChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun RenderDonutChart(chart: DonutChart, modifier: Modifier = Modifier) {
    val categories = chart.fieldNames
    val values = chart.data
    val colors = generateChartColors(categories.size)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(
            values = values,
            label = { index ->
                Text(
                    text = values[index].toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            slice = { index -> DefaultSlice(color = colors[index]) },
            holeSize = 0.6f,
            holeContent = {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = (values.sum().roundToInt() / 1.0f).toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        )
    }
}


/**
 * Renders an area chart based on the provided [AreaChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun RenderAreaChart(chart: AreaChart) {
    val sortedKeys = chart.data.keys.sorted()
    val minX = sortedKeys.firstOrNull() ?: 0f
    val maxX = sortedKeys.lastOrNull() ?: 0f
    val maxY = chart.data.values.maxOfOrNull { it.sum() } ?: 0f

    val seriesNames = chart.stackNames
    val seriesColors = generateChartColors(seriesNames.size)

    val seriesData = List(seriesNames.size) { seriesIndex ->
        sortedKeys.map { x -> chart.data[x]?.getOrNull(seriesIndex) ?: 0f }
    }

    val processedData = StackedAreaPlotDataAdapter(xData = sortedKeys, yData = seriesData)

    XYGraph(
        xAxisModel = rememberFloatLinearAxisModel(minX..maxOf(minX + 1f, maxX)),
        yAxisModel = rememberFloatLinearAxisModel(0f..maxOf(1f, maxY)),
        xAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {}
        ),
        yAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {}
        )
    ) {
        StackedAreaPlot(
            data = processedData,
            styles = seriesColors.map { col ->
                StackedAreaStyle(
                    lineStyle = LineStyle(SolidColor(col), strokeWidth = 2.dp),
                    areaStyle = AreaStyle(SolidColor(col.copy(alpha = 0.2f))),
                )
            },
            firstBaseline = AreaBaseline.HorizontalLine(0f),
        )
    }
}
