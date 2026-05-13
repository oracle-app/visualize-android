package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.oracle.visualize.presentation.components.charts.RenderAreaChart
import com.oracle.visualize.presentation.components.charts.RenderDonutChart
import com.oracle.visualize.presentation.components.charts.RenderHorizontalBarChart
import com.oracle.visualize.presentation.components.charts.RenderLineChart
import com.oracle.visualize.presentation.components.charts.RenderPieChart
import com.oracle.visualize.presentation.components.charts.RenderScatterChart
import com.oracle.visualize.presentation.components.charts.RenderStackedBarChart
import com.oracle.visualize.presentation.components.charts.RenderVerticalBarChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi

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
