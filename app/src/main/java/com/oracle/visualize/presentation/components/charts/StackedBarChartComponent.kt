package com.oracle.visualize.presentation.components.charts

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.presentation.components.generateChartColors
import com.oracle.visualize.ui.theme.ChartPalette
import io.github.koalaplot.core.animation.StartAnimationUseCase
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.StackedVerticalBarPlot
import io.github.koalaplot.core.gestures.GestureConfig
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.coroutines.launch

/**
 * Renders a stacked bar chart based on the provided [StackedBarChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 * @param showAxisLabels Enables or disables the property of axis labels to be shown.
 * @param enableTooltips Enables or disables the property of tooltips to be shown.
 * @param enableZoomAndPan Enables or disables the property of zooming and panning the chart.
 * @param feedCardLabels Changes the x-axis labels' sizes on a feed card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderStackedBarChart(
    modifier: Modifier = Modifier, chart: StackedBarChart, chartColorTheme: ChartPalette, showAxisLabels: Boolean,
    enableTooltips: Boolean, enableZoomAndPan: Boolean, feedCardLabels: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val categories = remember(chart.data) { listOf("") + chart.data.keys.toList() }
    val seriesCount = remember(chart.data) { chart.data.values.firstOrNull()?.size ?: 0 }
    val seriesLabel = stringResource(R.string.stacked_bar_series)

    val seriesNames = remember(seriesCount) {
        if (chart.stackNames.size >= seriesCount) {
            chart.stackNames
        } else {
            List(seriesCount) { i -> seriesLabel + "${i + 1}" }
        }
    }

    val seriesColors = remember(seriesNames.size) {
        generateChartColors(seriesNames.size, chartColorTheme)
    }

    val maxY = remember(chart.data.values) { chart.data.values.maxOfOrNull { it.sum() } ?: 0f }

    Box(modifier = modifier) {
        KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Gray, minorGridlineStyle = null)) {
            XYGraph(
                xAxisModel = rememberFloatLinearAxisModel(
                    range = 0f..categories.size.toFloat(),
                    minViewExtent = 1f,
                    minimumMajorTickIncrement = 1f,
                    minimumMajorTickSpacing = 10.dp
                ),
                yAxisModel = rememberFloatLinearAxisModel(
                    range = 0f..maxOf(1f, maxY),
                    minViewExtent = 0.01f,
                    minimumMajorTickIncrement = 0.01f,
                    minimumMajorTickSpacing = 30.dp
                ),
                xAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = {
                        if (it.toInt() in categories.indices) {
                            Text(
                                text = categories[it.toInt()],
                                modifier = Modifier.rotate(45f).padding(top = 8.dp),
                                fontSize = if (feedCardLabels) 8.sp else 10.sp,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    title = {
                        if (showAxisLabels && !chart.metrics.isEmpty()) {
                            Text(chart.metrics[0], style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                        }
                    }
                ),
                yAxisContent = AxisContent(
                    style = rememberAxisStyle(),
                    labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
                    title = {
                        if (showAxisLabels && !chart.metrics.isEmpty()) {
                            Box(modifier = Modifier.width(25.dp).height(1.dp).rotate(90f)) {
                                Text(
                                    text = chart.metrics[1],
                                    overflow = TextOverflow.Visible,
                                    softWrap = false,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                ),
                gestureConfig = GestureConfig(
                    zoomXEnabled = enableZoomAndPan,
                    zoomYEnabled = enableZoomAndPan,
                    panXEnabled = enableZoomAndPan,
                    panYEnabled = enableZoomAndPan,
                )
            ) {
                StackedVerticalBarPlot(
                    startAnimationUseCase = StartAnimationUseCase(
                        executionType = StartAnimationUseCase.ExecutionType.None,
                        chartAnimationSpecs = arrayOf(tween(0))
                    )
                ) {
                    if (categories.isNotEmpty() && seriesCount > 0) {
                        seriesNames.forEachIndexed { seriesIndex, _ ->
                            series {
                                categories.forEachIndexed { categoryIndex, category ->
                                    val categoryValues = chart.data[category]
                                    categoryValues?.size?.let {
                                        if (seriesIndex < it) {
                                            item(
                                                x = (categoryIndex).toFloat(),
                                                y = categoryValues[seriesIndex],
                                                bar = { _, itemIndex, plotEntry ->
                                                    if (!enableTooltips) {
                                                        DefaultBar(
                                                            brush = SolidColor(seriesColors[itemIndex])
                                                        )
                                                    } else {
                                                        val tooltipDisplayState = rememberTooltipState(
                                                            initialIsVisible = false, isPersistent = true
                                                        )

                                                        val currentYValue = plotEntry.y.end - plotEntry.y.start

                                                        TooltipBox(
                                                            tooltip = { PlainTooltip { Text(text = "${seriesNames[itemIndex]}: $currentYValue") } },
                                                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                                                positioning = TooltipAnchorPosition.Above,
                                                            ),
                                                            state = tooltipDisplayState
                                                        ) {
                                                            DefaultBar(
                                                                brush = SolidColor(seriesColors[itemIndex]),
                                                                modifier = Modifier.pointerInput(Unit) {
                                                                    awaitPointerEventScope {
                                                                        while (true) {
                                                                            val fingerEvent = awaitPointerEvent()
                                                                            if (fingerEvent.changes.size > 1) continue
                                                                            if (fingerEvent.type == PointerEventType.Release) {
                                                                                val change = fingerEvent.changes[0]
                                                                                if (change.changedToUp()) coroutineScope.launch { tooltipDisplayState.show() }
                                                                            }
                                                                        }
                                                                    }
                                                                    //detectTapGestures { coroutineScope.launch { tooltipDisplayState.show() } }
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
