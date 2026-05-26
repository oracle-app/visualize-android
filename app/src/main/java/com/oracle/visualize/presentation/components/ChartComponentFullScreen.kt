package com.oracle.visualize.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.domain.models.ScatterChart
import com.oracle.visualize.domain.models.VerticalBarChart
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend2
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.style.KoalaPlotTheme
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
    val localConfig = LocalConfiguration.current

    val labels = remember(chart)  { chart.fieldNames }

    val dSize = remember(chart.data) {
        when (chart.data) {
            is List<*> -> chart.data.size
            is Map<*,*> -> (chart.data.values.firstOrNull() as? List<*>)?.size ?: 1
            else -> 1
        }
    }

    val cleanLabels = labels.ifEmpty {
        List(dSize) { "${stringResource(R.string.chart_legend_cat_label)} ${it + 1}" }
    }

    val colors = remember(cleanLabels.size) {
        generateChartColors(cleanLabels.size)
    }

    when (chart) {
        is VerticalBarChart, is HorizontalBarChart, is LineChart, is ScatterChart -> {
            Column(modifier = modifier.fillMaxSize()) {
                Column(modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
                    .padding(horizontal = 10.dp).fillMaxSize()) {
                    ChartRenderGeneral(modifier, chart)
                }
            }
        }

        else -> {
            val flowLegendContent = @Composable {
                FlowLegend2(
                    itemCount = cleanLabels.size,
                    modifier = Modifier.wrapContentSize().padding(14.dp),
                    symbol = { Symbol(shape = CircleShape, fillBrush = SolidColor(colors[it])) },
                    label = { Text(cleanLabels[it], style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray) },
                    symbolGap = KoalaPlotTheme.sizes.gap,
                    columnGap = KoalaPlotTheme.sizes.gap,
                    rowGap = KoalaPlotTheme.sizes.gap
                )
            }

            ChartLayout(
                modifier = Modifier.fillMaxSize(),
                title = {},
                legend = {
                    when (localConfig.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Column(
                                modifier = Modifier.fillMaxHeight()
                                    .background(color = MaterialTheme.colorScheme.onPrimary),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                flowLegendContent()
                            }
                        }
                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.onPrimary),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                flowLegendContent()
                            }
                        }
                    }
                },
                legendLocation = when (localConfig.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> LegendLocation.LEFT
                    else -> LegendLocation.TOP
                }
            ) {
                Box(modifier = modifier.fillMaxSize()) {
                    Box(modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary)
                        .padding(horizontal = 10.dp).fillMaxSize()) {
                        ChartRenderGeneral(modifier, chart, showAxisLabels)
                    }
                }
            }
        }
    }
}
