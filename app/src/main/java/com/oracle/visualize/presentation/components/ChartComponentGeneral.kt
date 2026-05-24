package com.oracle.visualize.presentation.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
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
import com.oracle.visualize.ui.theme.ChartPalette
import io.github.koalaplot.core.animation.StartAnimationUseCase
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
fun ChartRenderGeneral(
    modifier: Modifier = Modifier, chart: Chart<*>, showAxisLabels: Boolean = true,
    enableTooltips: Boolean = true, enableZoomAndPan: Boolean = true
) {
    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
            .padding(top = 18.dp, start = 0.dp, end = 12.dp, bottom = 8.dp)
    ) {
        when (chart) {
            is VerticalBarChart -> RenderVerticalBarChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )

            is HorizontalBarChart -> RenderHorizontalBarChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )

            is StackedBarChart -> RenderStackedBarChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )

            is LineChart -> RenderLineChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )

            is ScatterChart -> RenderScatterChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )

            is PieChartModel -> RenderPieChart(chart = chart, enableTooltips = enableTooltips)

            is DonutChart -> RenderDonutChart(chart = chart, enableTooltips = enableTooltips)

            is AreaChart -> RenderAreaChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )
        }
    }
}


/** Generates a random color, based on a user's theme preference.
 *
 * @param n The amount of colors to be created.
 * @param colorTheme The user's preferred chart color palette.
 * @returns a list of [Color] objects.
 * */
fun generateChartColors(n: Int, colorTheme: ChartPalette): List<Color> {
    if (n <= 0) return emptyList()

    val colors = colorTheme.colors
    val colorsSize = colors.size

    return List(n) { i -> if (i < colorsSize) { colors[i] } else { colors[i % colorsSize] } }
}
