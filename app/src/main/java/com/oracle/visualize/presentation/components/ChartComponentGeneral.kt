package com.oracle.visualize.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
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
import kotlin.math.roundToInt

// Generates a random color. Will later be replaced by user's theme preference.
fun generateChartColors(n: Int): List<Color> {
    if (n <= 0) return emptyList()
    return List(n) { i ->
        Color.hsv(i * 360f / n, 0.6f, 0.9f)
    }
}

/**
 * Renders a chart based on the provided [Chart] data using KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ChartRenderGeneral(chart: Chart<*>, showTooltips: Boolean = true) {

    var plotIndex by remember { mutableStateOf<Int?>(null) }

    when (chart) {
        is VerticalBarChart -> {
            val categories = chart.data.keys.toList()
            val values = chart.data.values.toList()
            val maxValue = values.maxOrNull() ?: 0f
            val barColors = generateChartColors(categories.size)

            XYGraph(
                xAxisModel = remember { CategoryAxisModel(categories) },
                yAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it, style = MaterialTheme.typography.bodySmall) },
                    title = {}
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                    title = {}
                )
            ) {
                VerticalBarPlot(
                    xData = categories,
                    yData = values,
                    bar = { index, _, _ ->
                        DefaultBar(brush = SolidColor(barColors[index]), modifier = Modifier.fillMaxWidth())
                    }
                )
            Box {
                XYGraph(
                    xAxisModel = remember { CategoryAxisModel(categories) },
                    yAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
                    xAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { Text(it, style = MaterialTheme.typography.bodySmall) },
                        title = {}
                    ),
                    yAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                        title = {}
                    )
                ) {
                    VerticalBarPlot(
                        xData = categories,
                        yData = values,
                        bar = { index, _, _ ->
                            DefaultBar(
                                brush = SolidColor(barColors[index]),
                                modifier = Modifier.fillMaxWidth().then(
                                    if (showTooltips) {
                                        Modifier.clickable { plotIndex = if (plotIndex == index) null else index }
                                    } else Modifier
                                )
                            )
                        }
                    )
                }

                if (showTooltips && plotIndex != null) {
                    Popup(
                        alignment = Alignment.TopCenter,
                        offset = IntOffset(0, -100),
                        onDismissRequest = { plotIndex = null }
                    ) {
                        ChartTooltip(listOf("${categories[plotIndex!!]}: ${values[plotIndex!!]}"))
                    }
                }
            }
            }
        }

        is HorizontalBarChart -> {
            val categories = chart.data.keys.toList()
            val values = chart.data.values.toList()
            val maxValue = values.maxOrNull() ?: 0f
            val barColors = generateChartColors(categories.size)

            XYGraph(
                xAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
                yAxisModel = remember { CategoryAxisModel(categories) },
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                    title = {}
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it, style = MaterialTheme.typography.bodySmall) },
                    title = {}
                )
            ) {
                HorizontalBarPlot(
                    xData = values,
                    yData = categories,
                    bar = { index, _, _ ->
                        DefaultBar(brush = SolidColor(barColors[index]), modifier = Modifier.fillMaxHeight())
                    }
                )
            Box {
                XYGraph(
                    xAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
                    yAxisModel = remember { CategoryAxisModel(categories) },
                    xAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                        title = {}
                    ),
                    yAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { Text(it, style = MaterialTheme.typography.bodySmall) },
                        title = {}
                    )
                ) {
                    HorizontalBarPlot(
                        xData = values,
                        yData = categories,
                        bar = { index, _, _ ->
                            DefaultBar(
                                brush = SolidColor(barColors[index]),
                                modifier = Modifier.fillMaxWidth().then(
                                    if (showTooltips) {
                                        Modifier.clickable { plotIndex = if (plotIndex == index) null else index }
                                    } else Modifier
                                )
                            )
                        }
                    )
                }

                if (showTooltips && plotIndex != null) {
                    Popup(
                        alignment = Alignment.CenterEnd,
                        offset = IntOffset(0, 0),
                        onDismissRequest = { plotIndex = null }
                    ) {
                        ChartTooltip(listOf("${categories[plotIndex!!]}: ${values[plotIndex!!]}"))
                    }
                }
            }
            }
        }

        is StackedBarChart -> {
            val categories = chart.data.keys.toList()
            val maxY = chart.data.values.maxOfOrNull { it.sum() } ?: 0f
            val seriesNames = chart.stackNames
            val seriesColors = generateChartColors(seriesNames.size)

            if (categories.isNotEmpty()) {
                XYGraph(
                    xAxisModel = remember { CategoryAxisModel(categories) },
                    yAxisModel = rememberFloatLinearAxisModel(0f..maxOf(1f, maxY), minorTickCount = 0),
                    xAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { Text(it, style = MaterialTheme.typography.bodySmall) },
                        title = {}
                    ),
                    yAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                        title = {}
                    )
                ) {
                    StackedVerticalBarPlot {
                        seriesNames.forEachIndexed { seriesIndex, _ ->
                            series(
                                defaultBar = verticalSolidBar(seriesColors[seriesIndex], RectangleShape, border = null)
                            ) {
                                chart.data.forEach { (category, values) ->
                                    if (seriesIndex < values.size) item(category, values[seriesIndex])
                                }
                            }
                        }
                    }
                }
            }
        }

        is LineChart -> {
            val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
            val lineColor = generateChartColors(1).firstOrNull() ?: Color.Blue

            XYGraph (
                xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
                yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                    title = {}
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                    title = {}
                )
            ) {
                LinePlot(
                    data = processedData,
                    lineStyle = LineStyle(SolidColor(lineColor), strokeWidth = 2.dp)
                )
            }
        }

        is PieChartModel -> {
            val categories = chart.fieldNames
            val values = chart.data
            val colors = generateChartColors(categories.size)

            PieChart(
                values = values,
                label = { index -> Text(text = values[index].toString(),
                    modifier = Modifier.clickable { plotIndex = if (plotIndex == index) null else index }
                )},
                slice = { index -> DefaultSlice(color = colors[index])
                }
            )

            if (showTooltips && plotIndex != null) {
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(0, 0),
                    onDismissRequest = { plotIndex = null }
                ) {
                    ChartTooltip(listOf("${categories[plotIndex!!]}: ${values[plotIndex!!]}"))
                }
            }
        }

        is DonutChart -> {
            val categories = chart.fieldNames
            val values = chart.data
            val colors = generateChartColors(categories.size)

            PieChart(
                values = values,
                label = { index -> Text(text = values[index].toString(),
                    modifier = Modifier.clickable { plotIndex = if (plotIndex == index) null else index }
                )},
                slice = { index -> DefaultSlice(color = colors[index]) },
                holeSize = 0.6f,
                holeContent = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column {
                            Text(text = "Total", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = (values.sum().roundToInt() / 1.0f).toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            )

            if (showTooltips && plotIndex != null) {
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(0, 0),
                    onDismissRequest = { plotIndex = null }
                ) {
                    ChartTooltip(listOf("${categories[plotIndex!!]}: ${values[plotIndex!!]}"))
                }
            }
        }

        is ScatterChart -> {
            val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
            val dotColor = generateChartColors(1).firstOrNull() ?: Color.Blue

            XYGraph (
                xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
                yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                    title = {}
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
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

        is AreaChart -> {
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
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
                    title = {}
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall) },
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
    }
}
