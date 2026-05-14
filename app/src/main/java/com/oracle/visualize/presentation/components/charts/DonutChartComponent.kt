package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oracle.visualize.domain.models.DonutChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import kotlin.math.roundToInt

/**
 * Renders a donut chart based on the provided [DonutChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun RenderDonutChart(chart: DonutChart, modifier: Modifier = Modifier) {
    val categories = chart.fieldNames
    val values = chart.data
    val colors = generateChartColors(categories.size)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(
            values = values,
            label = { index ->
                Text(
                    text = values[index].toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            slice = { index -> DefaultSlice(color = colors[index]) },
            holeSize = 0.6f,
            holeContent = {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = (values.sum().roundToInt() / 1.0f).toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        )
    }
}
