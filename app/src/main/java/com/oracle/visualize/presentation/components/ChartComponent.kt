package com.oracle.visualize.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi

private fun generateColors(n: Int): List<Color> {
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
fun ChartRenderFullScreen(chart: Chart<*>) {
    when (chart) {
        is VerticalBarChart -> {
            val labels = chart.fieldNames
            val colors = remember(labels.size) {
                generateColors(labels.size)
            }

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = labels.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text("${labels[it]}") }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is HorizontalBarChart -> {
            val labels = chart.fieldNames
            val colors = generateColors(labels.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = labels.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text("${labels[it]}") }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is StackedBarChart -> {
            val colors = generateColors(chart.stackNames.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = chart.stackNames.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text(chart.stackNames[it]) }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is LineChart -> {
            val labels = chart.fieldNames
            val colors = generateColors(labels.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = labels.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text("${labels[it]}") }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is PieChartModel -> {
            val labels = chart.fieldNames
            val colors = generateColors(labels.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = labels.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text("${labels[it]}") }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is DonutChart -> {
            val labels = chart.fieldNames
            val colors = generateColors(labels.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = labels.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text("${labels[it]}") }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is ScatterChart -> {
            val labels = chart.fieldNames
            val colors = generateColors(labels.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = labels.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text("${labels[it]}") }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }

        is AreaChart -> {
            val colors = generateColors(chart.stackNames.size)

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                legend = {
                    FlowLegend(
                        modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                        itemCount = chart.stackNames.size,
                        symbol = { Symbol(shape = RectangleShape, fillBrush = SolidColor(colors[it])) },
                        label = { Text(chart.stackNames[it]) }
                    )
                },
                legendLocation = LegendLocation.TOP
            ) {
                ChartRenderFeedCard(chart)
            }
        }
    }
}