package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oracle.visualize.domain.models.DonutChart
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.toString
import kotlin.math.roundToInt

/**
 * Renders a donut chart based on the provided [DonutChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderDonutChart(
    modifier: Modifier = Modifier, chart: DonutChart,
    enableTooltips: Boolean
) {
    val categories = chart.fieldNames
    val values = chart.data
    val valuesTotal = values.sum()
    val percentageValues = values.map { value -> (value/valuesTotal) * 100 }
    val colors = generateChartColors(categories.size)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(
            values = values,
            label = { index ->
                val tooltipDisplayState = rememberTooltipState(
                    initialIsVisible = false, isPersistent = true
                )

                if (!enableTooltips && tooltipDisplayState.isVisible) {
                    tooltipDisplayState.dismiss()
                }

                TooltipBox(
                    tooltip = { PlainTooltip { Text(text = "${categories[index]}: ${values[index]}") } },
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        positioning = TooltipAnchorPosition.Above
                    ),
                    state = tooltipDisplayState,
                ) {
                    Text(text = "${percentageValues[index].toString(2)} %", color = Color.DarkGray)
                }
             },
            slice = { index -> DefaultSlice(color = colors[index]) },
            holeSize = 0.6f,
            holeContent = {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.DarkGray
                        )
                        Text(
                            text = (values.sum().roundToInt() / 1.0f).toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        )
    }
}
