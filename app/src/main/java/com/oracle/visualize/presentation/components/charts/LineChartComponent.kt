package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.presentation.components.generateChartColors
import com.oracle.visualize.presentation.components.rotateChartLegendLayout
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
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
@Composable
fun RenderLineChart(
    modifier: Modifier = Modifier,
    chart: LineChart,
    showAxisLabels: Boolean
) {
    val processedData = listOf(DefaultPoint(0f, 0f)) + chart.data.map { (x, y) -> DefaultPoint(x, y) }
    val lineColor = generateChartColors(1).firstOrNull() ?: Color.Blue

    XYGraph (
        xAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
        yAxisModel = rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
        xAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = { if (showAxisLabels && !chart.metrics.isEmpty()) Text(chart.metrics[0], style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer) }
        ),
        yAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = { Text(it.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) },
            title = {
                if (showAxisLabels && !chart.metrics.isEmpty()) {
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
            lineStyle = LineStyle(SolidColor(lineColor), strokeWidth = 2.dp)
        )
    }
}
