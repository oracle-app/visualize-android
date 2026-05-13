package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.oracle.visualize.domain.models.VerticalBarChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel

/**
 * Renders a vertical bar chart based on the provided [VerticalBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@Composable
fun RenderVerticalBarChart(chart: VerticalBarChart, modifier: Modifier = Modifier) {
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
                labels = {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
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
