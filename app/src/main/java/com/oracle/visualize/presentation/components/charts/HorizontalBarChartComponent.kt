package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.HorizontalBarPlot
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
@Composable
fun RenderHorizontalBarChart(chart: HorizontalBarChart, modifier: Modifier = Modifier) {
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
