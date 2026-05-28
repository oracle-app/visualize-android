package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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

    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
            .padding(top = 18.dp, start = 0.dp, end = 12.dp, bottom = 8.dp)
    ) {
        when (chart) {
            is VerticalBarChart -> {
                barCount = chart.fieldNames.size
                scrollable = barCount > 10 && !feedCardLabels
                chartWidth = if (scrollable) (barCount * 100).dp else 0.dp
                boxModifier = if (scrollable) Modifier.fillMaxWidth().horizontalScroll(scrollState) else Modifier.fillMaxWidth()
                chartModifier = if (!scrollable) Modifier.fillMaxWidth() else Modifier.width(chartWidth)

                Box(modifier = boxModifier) {
                    RenderVerticalBarChart(
                        modifier = chartModifier, chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips,
                        enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
                    )
                }
            }

            is HorizontalBarChart -> {
                RenderHorizontalBarChart(chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips,
                        enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels)
            }

            is StackedBarChart -> {
                barCount = chart.data.size
                scrollable = barCount > 10 && !feedCardLabels
                chartWidth = if (scrollable) (barCount * 100).dp else 0.dp
                boxModifier = if (scrollable) Modifier.fillMaxWidth().horizontalScroll(scrollState) else Modifier.fillMaxWidth()
                chartModifier = if (!scrollable) Modifier.fillMaxWidth() else Modifier.width(chartWidth)

                Box(modifier = boxModifier) {
                    RenderStackedBarChart(
                        modifier = chartModifier, chart = chart, showAxisLabels = showAxisLabels,
                        enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan,
                        feedCardLabels = feedCardLabels
                    )
                }
            }

            is LineChart -> RenderLineChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips,
                enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
            )

            is ScatterChart -> RenderScatterChart(
                chart = chart, showAxisLabels = showAxisLabels, enableTooltips = enableTooltips,
                enableZoomAndPan = enableZoomAndPan, feedCardLabels = feedCardLabels
            )

            is PieChartModel -> RenderPieChart(chart = chart, enableTooltips = enableTooltips)

            is DonutChart -> RenderDonutChart(chart = chart, enableTooltips = enableTooltips)

            is AreaChart -> RenderAreaChart(chart = chart, showAxisLabels = showAxisLabels,
                enableTooltips = enableTooltips, enableZoomAndPan = enableZoomAndPan)
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
