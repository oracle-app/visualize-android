package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.AreaChart
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.DonutChart
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.domain.models.PieChartModel
import com.oracle.visualize.domain.models.ScatterChart
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.domain.models.TileChart
import com.oracle.visualize.domain.models.VerticalBarChart
import com.oracle.visualize.presentation.components.charts.RenderAreaChart
import com.oracle.visualize.presentation.components.charts.RenderDonutChart
import com.oracle.visualize.presentation.components.charts.RenderHorizontalBarChart
import com.oracle.visualize.presentation.components.charts.RenderLineChart
import com.oracle.visualize.presentation.components.charts.RenderPieChart
import com.oracle.visualize.presentation.components.charts.RenderScatterChart
import com.oracle.visualize.presentation.components.charts.RenderStackedBarChart
import com.oracle.visualize.presentation.components.charts.RenderTileChart
import com.oracle.visualize.presentation.components.charts.RenderVerticalBarChart
import com.oracle.visualize.ui.theme.ChartPalette
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
    modifier: Modifier = Modifier,
    chart: Chart<*>,
    chartColorTheme: ChartPalette = ChartPalette.THEME1,
    showAxisLabels: Boolean = true,
    enableTooltips: Boolean = true,
    enableZoomAndPan: Boolean = true,
    feedCardLabels: Boolean = false
) {
    val scrollState = rememberScrollState()
    var chartWidth: Dp
    var barCount: Int
    var scrollable: Boolean
    var boxModifier: Modifier
    var chartModifier: Modifier

    val topPadding = if (feedCardLabels) 8.dp else 18.dp
    val bottomPadding = if (feedCardLabels) 8.dp else 8.dp
    val sidePadding = if (feedCardLabels) 6.dp else 6.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary)
            .padding(top = topPadding, start = sidePadding, end = sidePadding, bottom = bottomPadding)
    ) {
        when (chart) {
            is VerticalBarChart -> {
                RenderVerticalBarChart(
                    modifier = Modifier.fillMaxSize(), chart = chart, showAxisLabels = showAxisLabels, chartColorTheme = chartColorTheme,
                    enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
                )
            }

            is HorizontalBarChart -> {
                RenderHorizontalBarChart(
                    modifier = Modifier.fillMaxSize(), chart = chart, showAxisLabels = showAxisLabels, chartColorTheme = chartColorTheme,
                    enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
                )
            }

            is StackedBarChart -> {
                RenderStackedBarChart(
                    modifier = Modifier.fillMaxSize(), chart = chart, showAxisLabels = showAxisLabels, chartColorTheme = chartColorTheme,
                    enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
                )
            }

            is LineChart -> RenderLineChart(
                chart = chart, showAxisLabels = showAxisLabels, chartColorTheme = chartColorTheme,
                enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
            )

            is ScatterChart -> RenderScatterChart(
                chart = chart, showAxisLabels = showAxisLabels, chartColorTheme = chartColorTheme,
                enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
            )

            is PieChartModel -> RenderPieChart(chart = chart, chartColorTheme = chartColorTheme, enableTooltips = enableTooltips)

            is DonutChart -> RenderDonutChart(chart = chart, chartColorTheme = chartColorTheme, enableTooltips = enableTooltips)

            is AreaChart -> RenderAreaChart(
                chart = chart, showAxisLabels = showAxisLabels, chartColorTheme = chartColorTheme,
                enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan
            )
            is TileChart -> RenderTileChart(
                modifier = Modifier.fillMaxSize(),
                chart = chart,
                chartColorTheme = chartColorTheme,
                isFeedCard = feedCardLabels
            )
        }
    }
}


/** Generates a random color, based on a user's theme preference.
 *
 * @param n The amount of colors to be created.
 * @param colorTheme The user's preferred chart color palette.
 * @param isBarChart Activates a special palette for vertical and horizontal bar charts.
 * @returns a list of [Color] objects.
 * */
fun generateChartColors(n: Int, colorTheme: ChartPalette, isBarChart: Boolean = false): List<Color> {
    if (n <= 0) return emptyList()

    val colors = colorTheme.colors
    val colorsSize = colors.size
    val finalColorsList = List(n) { i -> if (i < colorsSize) { colors[i] } else { colors[i % colorsSize] } }

    return if (isBarChart) listOf(Color.Transparent) + finalColorsList else finalColorsList
}
