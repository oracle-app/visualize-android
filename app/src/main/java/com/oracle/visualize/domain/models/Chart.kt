package com.oracle.visualize.domain.models

enum class ChartType {
    BAR,
    LINE,
    PIE,
    SCATTER,
    AREA,
    DONUT
}

class Chart {

    fun drawChart(xPoints: List<Int>, yPoints: List<Int>, typeChart: ChartType) {
        when (typeChart) {
            ChartType.BAR -> drawBar(xPoints, yPoints)
        }
    }

    private fun drawBar(xPoints: List<Int>, yPoint: List<Int>){}
}