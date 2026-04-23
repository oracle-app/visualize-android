package com.oracle.visualize.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.ChartDataSeries
import com.oracle.visualize.domain.models.ChartTypes
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel

private fun buildLineChartData(xData: List<Float>, yData: List<Float>): List<DefaultPoint<Float, Float>> {
    val n = xData.size
    return List(n) { i ->
        DefaultPoint(xData[i], yData[i])
    }
}

private fun chartColorGenerator(n: Int): List<Color> {
    return List(n) { i ->
        Color.hsv(i * 360f / n, 0.6f, 0.9f)
    }
}

private fun getMaxYBarChart(series: List<ChartDataSeries>): Float {
    var maxValue = 0f
    series.forEach { series ->
        if (series.yValues.isNotEmpty() && series.yValues.max() > maxValue) {
            maxValue = series.yValues.max()
        }
    }
    return maxValue
}

/*
* CAUTION: Implement the rendering algorithm when the charts microservice JSON is defined.
* */
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun RenderChart(chart: Chart) {
    val series = chart.series
    if (series.isEmpty()) return
    val numCategories = series.size
    val colors = chartColorGenerator(numCategories)

    when (chart.chartType) {
        ChartTypes.VERTICAL_BAR -> {
            val maxY: Float = getMaxYBarChart(chart.series)
            XYGraph(
                xAxisModel = remember { CategoryAxisModel(chart.categories) },
                yAxisModel = rememberFloatLinearAxisModel(
                    0f..maxY,
                    minorTickCount = 0),
                yAxisTitle = chart.chartTitle
            ) {
                VerticalBarPlot(
                    xData = chart.categories,
                    yData = chart.series[0].yValues,
                    bar = { _, _, _ ->
                        DefaultBar(
                            brush = SolidColor(colors[0]),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }

        ChartTypes.LINE -> {
            XYGraph(
                rememberFloatLinearAxisModel(0f..chart.series[0].xValues.max()),
                rememberFloatLinearAxisModel(0f..chart.series[0].yValues.max()),
                yAxisTitle = chart.chartTitle

            ) {
                chart.series.forEachIndexed { idx, series ->
                    val data = buildLineChartData(series.xValues, series.yValues)
                    LinePlot(
                        data,
                        lineStyle = LineStyle(SolidColor(colors[idx]))
                    )
                }
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chart")
            }
        }
    }
}
