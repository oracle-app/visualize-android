package com.oracle.visualize.presentation.components.charts

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.presentation.components.generateChartColors
import com.oracle.visualize.ui.theme.ChartPalette
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.gestures.GestureConfig
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Renders a line chart based on the provided [LineChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 * @param showAxisLabels Enables or disables the property of axis labels to be shown.
 * @param enableTooltips Enables or disables the property of tooltips to be shown.
 */
@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderLineChart(
    modifier: Modifier = Modifier, chart: LineChart, showAxisLabels: Boolean,
    enableTooltips: Boolean, enableZoomAndPan: Boolean, feedCardLabels: Boolean
) {
    val processedData = remember (chart.data) {
        listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
    }
    val lineColor = generateChartColors(1, ChartPalette.THEME1).firstOrNull() ?: Color.Blue
    val dotColors = generateChartColors(2, ChartPalette.THEME1)

    var xMetric = stringResource(R.string.line_scatter_x_metric)
    var yMetric = stringResource(R.string.line_scatter_y_metric)

    if (!chart.metrics.isEmpty()) {
        xMetric = chart.metrics[0].ifBlank { xMetric }
        yMetric = chart.metrics[1].ifBlank { yMetric }
    }

    KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Gray, minorGridlineStyle = null)) {
        XYGraph (
            xAxisModel = rememberFloatLinearAxisModel(
                range = processedData.autoScaleXRange(),
                minViewExtent = 0.01f,
                minimumMajorTickIncrement = 0.01f,
                minimumMajorTickSpacing = 60.dp
            ),
            yAxisModel = rememberFloatLinearAxisModel(
                range = processedData.autoScaleYRange(),
                minViewExtent = 0.01f,
                minimumMajorTickIncrement = 0.01f,
                minimumMajorTickSpacing = 30.dp
            ),
            xAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
                title = {
                    if (showAxisLabels) {
                        Text(xMetric, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                    }
                }
            ),
            yAxisContent = AxisContent(
                style = rememberAxisStyle(),
                labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) },
                title = {
                    if (showAxisLabels) {
                        Box(modifier = modifier
                            .width(25.dp)
                            .height(1.dp)
                            .rotate(90f)) {
                            Text(
                                text = yMetric,
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
            LinePlot2(
                data = processedData,
                lineStyle = LineStyle(SolidColor(lineColor), strokeWidth = 2.dp),
                animationSpec = tween(0)
            )

            if (enableTooltips) {
                LinePlot2(
                    data = processedData,
                    symbol = { plotPoint ->
                        val coroutineScope = rememberCoroutineScope()

                        val tooltipDisplayState = rememberTooltipState(
                            initialIsVisible = false, isPersistent = true
                        )

                        TooltipBox(
                            tooltip = { PlainTooltip { Text(text = "$xMetric: ${plotPoint.x}\n$yMetric: ${plotPoint.y}") } },
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                positioning = TooltipAnchorPosition.Above
                            ),
                            state = tooltipDisplayState,
                        ) {
                            Symbol(
                                modifier = Modifier.size(30.dp).pointerInput(Unit) {
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
                                },
                                fillBrush = SolidColor(dotColors[0]),
                                outlineBrush = SolidColor(dotColors[1]),
                                outlineStroke = Stroke(width = 4f),
                                shape = CircleShape
                            )
                        }
                    },
                    animationSpec = tween(0)
                )
            }
        }
    }
}
