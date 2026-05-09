package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.ChartDTO
import com.oracle.visualize.domain.models.*
import com.oracle.visualize.domain.models.enums.ChartTypes

/**
 * Extension function to map [ChartDTO] to [Chart] domain model, returning a specific
 * chart implementation based on its type.
 *
 * @return A [Chart] object.
 */
import org.json.JSONObject
import com.oracle.visualize.domain.models.*

object ChartMapper {

    fun fromPreviewJson(previewJson: String): Chart<*>? {
        if (previewJson.isBlank() || previewJson == "{}") return null

        return try {
            val jsonObject = JSONObject(previewJson)
            val chartName = jsonObject.optString("chartName", "Sin Título")
            val chartTypeStr = jsonObject.optString("chartType", "")

            val metricsObj = jsonObject.optJSONObject("metrics")
            val fieldNames = mutableListOf<String>()
            metricsObj?.let {
                val keys = it.keys()
                while (keys.hasNext()) {
                    fieldNames.add(it.getString(keys.next()))
                }
            }

            val dataObj = jsonObject.optJSONObject("data")

            when (chartTypeStr) {
                "Vertical Bar Chart" -> {
                    VerticalBarChart(chartName, parseToMapStringFloat(dataObj), fieldNames)
                }
                "Horizontal Bar Chart" -> {
                    HorizontalBarChart(chartName, parseToMapStringFloat(dataObj), fieldNames)
                }
                "Stacked Bar Chart" -> {
                    val stackNames = fieldNames.drop(1)
                    StackedBarChart(chartName, parseToMapStringListFloat(dataObj), stackNames)
                }
                "Line Chart", "Line" -> {
                    LineChart(chartName, parseToMapFloatFloat(dataObj), fieldNames)
                }
                "Pie Chart", "Pie" -> {
                    PieChartModel(chartName, parseToListFloat(dataObj), fieldNames)
                }
                "Donut Chart", "Donut" -> {
                    DonutChart(chartName, parseToListFloat(dataObj), fieldNames)
                }
                "Scatter Chart", "Scatter" -> {
                    ScatterChart(chartName, parseToMapFloatFloat(dataObj), fieldNames)
                }
                "Area Chart", "Area" -> {
                    val stackNames = fieldNames.drop(1)
                    AreaChart(chartName, parseToMapFloatListFloat(dataObj), stackNames)
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseSingleKpi(dataObj: JSONObject?): Float {
        if (dataObj == null) return 0f
        val keysArray = dataObj.optJSONArray("field1") ?: dataObj.optJSONArray("field2")
        return keysArray?.optString(0)?.toFloatOrNull() ?: 0f
    }

    private fun parseToMapStringFloat(dataObj: JSONObject?): Map<String, Float> {
        val map = mutableMapOf<String, Float>()
        if (dataObj == null) return map

        val keysArray = dataObj.optJSONArray("field1")
        val valuesArray = dataObj.optJSONArray("field2")

        if (keysArray != null && valuesArray != null) {
            for (i in 0 until keysArray.length()) {
                val key = keysArray.optString(i)
                val value = valuesArray.optString(i).toFloatOrNull() ?: 0f
                map[key] = value
            }
        }
        return map
    }

    private fun parseToMapStringListFloat(dataObj: JSONObject?): Map<String, List<Float>> {
        val map = mutableMapOf<String, List<Float>>()
        if (dataObj == null) return map

        val keysArray = dataObj.optJSONArray("field1") ?: return map

        for (i in 0 until keysArray.length()) {
            val key = keysArray.optString(i)
            val valueList = mutableListOf<Float>()

            var fieldIndex = 2
            while (dataObj.has("field$fieldIndex")) {
                val array = dataObj.optJSONArray("field$fieldIndex")
                valueList.add(array?.optString(i)?.toFloatOrNull() ?: 0f)
                fieldIndex++
            }
            map[key] = valueList
        }
        return map
    }

    private fun parseToMapFloatFloat(dataObj: JSONObject?): Map<Float, Float> {
        val map = mutableMapOf<Float, Float>()
        if (dataObj == null) return map

        val keysArray = dataObj.optJSONArray("field1")
        val valuesArray = dataObj.optJSONArray("field2")

        if (keysArray != null && valuesArray != null) {
            for (i in 0 until keysArray.length()) {
                val key = keysArray.optString(i).toFloatOrNull() ?: 0f
                val value = valuesArray.optString(i).toFloatOrNull() ?: 0f
                map[key] = value
            }
        }
        return map
    }

    private fun parseToMapFloatListFloat(dataObj: JSONObject?): Map<Float, List<Float>> {
        val map = mutableMapOf<Float, List<Float>>()
        if (dataObj == null) return map

        val keysArray = dataObj.optJSONArray("field1") ?: return map

        for (i in 0 until keysArray.length()) {
            val key = keysArray.optString(i).toFloatOrNull() ?: 0f
            val valueList = mutableListOf<Float>()

            var fieldIndex = 2
            while (dataObj.has("field$fieldIndex")) {
                val array = dataObj.optJSONArray("field$fieldIndex")
                valueList.add(array?.optString(i)?.toFloatOrNull() ?: 0f)
                fieldIndex++
            }
            map[key] = valueList
        }
        return map
    }

    private fun parseToListFloat(dataObj: JSONObject?): List<Float> {
        val list = mutableListOf<Float>()
        if (dataObj == null) return list

        val valuesArray = dataObj.optJSONArray("field2") ?: dataObj.optJSONArray("field1")

        if (valuesArray != null) {
            for (i in 0 until valuesArray.length()) {
                val value = valuesArray.optString(i).toFloatOrNull() ?: 0f
                list.add(value)
            }
        }
        return list
    }
}
