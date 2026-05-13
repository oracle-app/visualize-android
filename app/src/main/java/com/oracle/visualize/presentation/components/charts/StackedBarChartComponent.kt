package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.bar.StackedVerticalBarPlot
import io.github.koalaplot.core.bar.verticalSolidBar
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
@Composable
fun RenderStackedBarChart(
    modifier: Modifier = Modifier,
    chart: StackedBarChart,
    showAxisLabels: Boolean
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

    if (categories.isNotEmpty() && seriesCount > 0) {
        XYGraph(
            xAxisModel = remember { CategoryAxisModel(categories) },
            yAxisModel = rememberFloatLinearAxisModel(0f..maxOf(1f, maxY), minorTickCount = 0),
            xAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = { if (showAxisLabels && !chart.metrics.isEmpty()) Text(chart.metrics[0], style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer) }
            ),
            yAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                title = {
                    if (showAxisLabels && !chart.metrics.isEmpty()) {
                        Box(modifier = modifier.width(25.dp).height(1.dp).rotate(90f)) {
                            Text(
                                text = chart.metrics[1],
                                overflow = TextOverflow.Visible,
                                softWrap = false,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
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
