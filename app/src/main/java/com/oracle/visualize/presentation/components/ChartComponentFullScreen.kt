package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.domain.models.ScatterChart
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi

/**
 * Renders a chart based on the provided [Chart] data using KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The modifier object that lets a parent component modify its
 * appearance.
 */

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ChartRenderFullScreen(
    modifier: Modifier = Modifier,
    chart: Chart<*>,
    showAxisLabels: Boolean = true
) {
    val labels = chart.fieldNames

    val dSize = when (chart.data) {
        is List<*> -> chart.data.size
        is Map<*,*> -> (chart.data.values.firstOrNull() as? List<*>)?.size ?: 1
        else -> 1
    }

    val cleanLabels = labels.ifEmpty {
        List(dSize) { "Cat ${it + 1}" }
    }

    val colors = generateChartColors(cleanLabels.size)

    when (chart) {
        is LineChart, is ScatterChart -> {
            Column(modifier = modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(60.dp))
                Column(modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
                    .padding(horizontal = 10.dp).fillMaxSize()) {
                    ChartRenderGeneral(modifier, chart)
                }
            }
        }
        else -> {
            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = {},
                legend = {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
                        FlowLegend(
                            modifier = Modifier
                                .border(1.dp,
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.onPrimary,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp).align(Alignment.Center),
                            itemCount = cleanLabels.size,
                            symbol = { Symbol(
                                shape = CircleShape,
                                fillBrush = SolidColor(colors[it])
                            ) },
                            label = {
                                Text(
                                    cleanLabels[it],
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.DarkGray
                                ) }
                        )
                    }
                },
                legendLocation = LegendLocation.TOP
            ) {
                Column(modifier = modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Column(modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
                        .padding(horizontal = 10.dp).fillMaxSize()) {
                        ChartRenderGeneral(modifier, chart, showAxisLabels)
                    }
                }
            }
        }
    }
}
