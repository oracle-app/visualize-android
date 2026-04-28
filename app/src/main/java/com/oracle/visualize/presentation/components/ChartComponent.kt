package com.oracle.visualize.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.domain.models.VerticalBarChart
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.HorizontalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel

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
                yAxisTitle = chart.chartTitle
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

        else -> {
            throw IllegalArgumentException("Invalid chart type")
        }
    }
}