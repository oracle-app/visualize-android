package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.Symbol
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
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Renders a line chart based on the provided [LineChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderLineChart(
    modifier: Modifier = Modifier, chart: LineChart, showAxisLabels: Boolean,
    enableTooltips: Boolean
) {
    val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
    val lineColor = generateChartColors(1).firstOrNull() ?: Color.Blue

    var xMetric = "x"
    var yMetric = "y"

    if (!chart.metrics.isEmpty()) {
        xMetric = chart.metrics[0].ifBlank { "" }
        yMetric = chart.metrics[1].ifEmpty { "" }
    }

    KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Gray, minorGridlineStyle = null)) {
        XYGraph (
            xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
            yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
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
            )
        ) {
            LinePlot2(
                data = processedData,
                lineStyle = LineStyle(SolidColor(lineColor), strokeWidth = 2.dp)
            )

            LinePlot2(
                data = processedData,
                symbol = { plotPoint ->
                    val tooltipDisplayState = rememberTooltipState(
                        initialIsVisible = false, isPersistent = true
                    )

                    if (!enableTooltips && tooltipDisplayState.isVisible) {
                        tooltipDisplayState.dismiss()
                    }

                    TooltipBox(
                        tooltip = { PlainTooltip { Text(text = "$xMetric: ${plotPoint.x}\n$yMetric: ${plotPoint.y}") } },
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            positioning = TooltipAnchorPosition.Above
                        ),
                        state = tooltipDisplayState,
                    ) {
                        Symbol(fillBrush = SolidColor(lineColor), shape = CircleShape)
                    }
                }
            )
        }
    }
}
