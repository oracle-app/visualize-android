package com.oracle.visualize.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.AreaChart
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.StackedBarChart
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi

/**
 * Renders a chart based on the provided [Chart] data using KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ChartRenderFullScreen(chart: Chart<*>) {
    val labels = when (chart) {
        is AreaChart -> chart.stackNames
        is StackedBarChart -> chart.stackNames
        else -> chart.fieldNames
    }

    val colors = generateChartColors(labels.size)

    ChartLayout(
        modifier = Modifier.fillMaxSize(),
        title = { Text("", style = MaterialTheme.typography.titleLarge) },
        legend = {
            FlowLegend(
                modifier = Modifier.padding(bottom = 36.dp).border(1.dp, Color.Black).padding(16.dp),
                itemCount = labels.size,
                symbol = { Symbol(shape = CircleShape, fillBrush = SolidColor(colors[it])) },
                label = { Text("${labels[it]}") }
            )
        },
        legendLocation = LegendLocation.TOP
    ) { ChartRenderGeneral(chart) }
}