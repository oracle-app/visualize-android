package com.oracle.visualize.presentation.components.charts

import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oracle.visualize.domain.models.PieChartModel
import com.oracle.visualize.presentation.components.generateChartColors
import com.oracle.visualize.ui.theme.ChartPalette
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
 * @param enableTooltips Enables or disables the property of tooltips to be shown.
 */
@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderPieChart(modifier: Modifier = Modifier, chart: PieChartModel, enableTooltips: Boolean) {
    val coroutineScope = rememberCoroutineScope()
    val categories = chart.fieldNames
    val values = chart.data
    val valuesTotal = values.sum()
    val percentageValues = values.map { value -> (value/valuesTotal) * 100 }
    val colors = generateChartColors(categories.size, ChartPalette.THEME1)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(
            values = values,
            label = { index ->
                PieDonutToolTipLabelBox(
                    percentageValue = percentageValues[index], categoryName = categories[index],
                    categoryValue = values[index], enableTooltip = enableTooltips, coroutineScope = coroutineScope
                )
            },
            slice = { index -> DefaultSlice(color = colors[index]) },
            pieAnimationSpec = tween(0),
            labelAnimationSpec = snap(),
        )
    }
}
