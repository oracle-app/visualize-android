package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oracle.visualize.domain.models.PieChartModel
import com.oracle.visualize.presentation.components.generateChartColors
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi

/**
 * Renders a pie chart based on the provided [PieChart] data using
 * KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun RenderPieChart(modifier: Modifier = Modifier, chart: PieChartModel) {
    val categories = chart.fieldNames
    val values = chart.data
    val colors = generateChartColors(categories.size)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(
            values = values,
            label = { index -> Text(text = values[index].toString(), color = Color.DarkGray) },
            slice = { index -> DefaultSlice(color = colors[index]) }
        )
    }
}
