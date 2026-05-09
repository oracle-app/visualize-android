package com.oracle.visualize.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.PieChartModel
import com.oracle.visualize.domain.models.VerticalBarChart
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import com.oracle.visualize.R


private fun buildLineChartData(xData: List<Float>, yData: List<Float>): List<DefaultPoint<Float, Float>> {
    val n = xData.size
    return List(n) { i ->
        DefaultPoint(xData[i], yData[i])
    }
}

private fun generateColors(n: Int): List<Color> {
    return List(n) { i ->
        Color.hsv(i * 360f / n, 0.6f, 0.9f)
    }
}


/*
* CAUTION: Implement the rendering algorithm when the charts microservice JSON is defined.
* */

/**
 * Renders a chart based on the provided [Chart] data using KoalaPlot.
 *
 * @param chart The chart configuration and data to render.
 */
 
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ChartRender(chart: Chart<*>) {
    when (chart) {
        is VerticalBarChart -> {
            val data = chart.data
            val values = data.values.toList()
            val maxValue = values.maxOrNull() ?: 0f
            val labels = chart.fieldNames.values.toList()
            val colors = remember(labels.size) {
                generateColors(labels.size)
            }

            XYGraph(
                xAxisModel = remember { CategoryAxisModel(labels) },
                yAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
                yAxisTitle = ""
            ) {
                VerticalBarPlot(
                    xData = labels,
                    yData = values,
                    bar = { index, _, _ ->
                        DefaultBar(
                            brush = SolidColor(colors[index]),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }

//        TODO("Still need to fix Horizontal and Line Charts")
//
//        is HorizontalBarChart -> {
//            val data = chart.data
//            val values = data.values.toList()
//            val maxValue = values.maxOrNull() ?: 0f
//            val labels = mutableListOf<String>()
//
//            for ((key, value) in chart.fieldNames) {
//                labels.add(value)
//            }
//
//            val colors = generateColors(labels.size)
//
//            XYGraph(
//                xAxisModel = remember { CategoryAxisModel(labels) },
//                yAxisModel = rememberFloatLinearAxisModel(0f..maxValue, minorTickCount = 0),
//                yAxisTitle = chart.chartTitle
//            ) {
//                HorizontalBarPlot(
//                    xData = labels,
//                    yData = values,
//                    bar = { index, _, _ ->
//                        DefaultBar(
//                            brush = SolidColor(colors[index]),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                )
//            }
//        }
//
//        is LineChart -> {
//            val data = chart.data
//            val processedData = mutableListOf<DefaultPoint<Float, Float>>()
//
//            for ((x, y) in data) {
//                processedData.add(DefaultPoint(x, y))
//            }
//
//            val values = data.values.toList()
//            val labels = mutableListOf<String>()
//
//            for ((key, value) in chart.fieldNames) {
//                labels.add(value)
//            }
//
//            val colors = generateColors(labels.size)
//
//            XYGraph (
//                rememberFloatLinearAxisModel(processedData.autoScaleXRange()),
//                rememberFloatLinearAxisModel(processedData.autoScaleYRange()),
//            ) {
//                for (i in 0..processedData.size) {
//                    Line(
//                        processedData[i],
//                        lineStyle = LineStyle(SolidColor(colors[i]))
//                    )
//                }
//            }
//        }

        is PieChartModel -> {
            PieChart(
                chart.data,
                label = {
                    i -> Text(text = chart.data[i].toString())
                }
            )
        }

        else -> {
            Text(stringResource(R.string.invalid_chart))
        }
    }
}
