package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.Chart
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
fun ChartRenderFullScreen(chart: Chart<*>, modifier: Modifier = Modifier) {
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

    ChartLayout(
        modifier = Modifier.fillMaxSize(),
        title = { Text("", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer) },
        legend = {
            FlowLegend(
                modifier = Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                itemCount = cleanLabels.size,
                symbol = { Symbol(
                    shape = CircleShape,
                    fillBrush = SolidColor(colors[it])
                ) },
                label = {
                    Text(
                        cleanLabels[it],
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ) }
            )
        },
        legendLocation = LegendLocation.TOP
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(60.dp))
            ChartRenderGeneral(chart)
        }
    }
}
