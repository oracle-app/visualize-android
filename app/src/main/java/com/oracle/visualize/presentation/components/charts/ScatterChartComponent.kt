package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.ScatterChart
import com.oracle.visualize.presentation.components.generateChartColors
import com.oracle.visualize.presentation.components.rotateChartLegendLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.LinePlot
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
 * Renders a scatter plot chart based on the provided [ScatterChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun RenderScatterChart(
    modifier: Modifier = Modifier,
    chart: ScatterChart,
    showAxisLabels: Boolean
) {
    val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
    val dotColor = generateChartColors(1).firstOrNull() ?: Color.Blue

    XYGraph (
        xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
        yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
        xAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = { if (showAxisLabels) Text(chart.metrics[0], style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer) }
        ),
        yAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {
                if (showAxisLabels) {
                    Box(modifier = modifier.width(25.dp).height(1.dp).rotate(90f)) {
                        Text(
                            text = chart.metrics[1],
                            overflow = TextOverflow.Visible,
                            softWrap = false,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        )
    ) {
        LinePlot(
            data = processedData,
            symbol = {
                Symbol(fillBrush = SolidColor(dotColor), outlineBrush = SolidColor(dotColor))
            },
        )
    }
}
