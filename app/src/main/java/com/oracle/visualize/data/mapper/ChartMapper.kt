package com.oracle.visualize.data.mapper

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.*
import org.json.JSONException

/**
 * Extension function to parse JSON in the DB to [Chart] domain model, returning a specific
 * chart implementation based on its type.
 *
 * @return A [Chart] object.
 */
import org.json.JSONObject
object ChartMapper {

    /**
     * Function to parse a JSON into a Chart object.
     *
     * @param previewJson The JSON code to process.
     * @return a Chart object, depending on the type of chart.
    **/
    fun fromPreviewJson(previewJson: String): Chart<*>? {
        if (previewJson.isBlank() || previewJson == "{}") return null

        return try {
            val jsonObject = JSONObject(previewJson)
            val chartName = jsonObject.optString("chartName", "No Title")
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

            var stackNames = if (metricsObj !== null) fieldNames.drop(1) else emptyList()

            if (dataObj != null && stackNames.isEmpty() ) {
                val field2 = dataObj.optJSONObject("field2")
                if (field2 !== null) {
                    val f2Keys = field2.keys()
                    val f2KeysList = mutableListOf<String>()
                    while (f2Keys.hasNext()) {
                        f2KeysList.add(f2Keys.next())
                    }
                    stackNames = f2KeysList
                }
            }

            when (chartTypeStr) {
                "Vertical Bar Chart" -> {
                    VerticalBarChart(chartName, parseToMapStringFloat(dataObj), fieldNames)
                }
                "Horizontal Bar Chart" -> {
                    HorizontalBarChart(chartName, parseToMapStringFloat(dataObj), fieldNames)
                }
                "Stacked Bar Chart" -> {

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
                    AreaChart(chartName, parseToMapFloatListFloat(dataObj), stackNames)
                }
                else -> {
                    throw AppError.ParsingError("Unsupported chart type: $chartTypeStr")
                }
            }
        }catch (e: JSONException) {
            throw AppError.ParsingError("JSON parsing error: ${e.message}")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.ParsingError("Error parsing chart: ${e.message}")
        }
    }

    /**
     * Function to process a JSON object to obtain the KPI.
     *
     * @param dataObj The JSON data object to process.
     * @return the KPI in Float type.
     **/
    private fun parseSingleKpi(dataObj: JSONObject?): Float {
        if (dataObj == null) return 0f
        val keysArray = dataObj.optJSONArray("field1") ?: dataObj.optJSONArray("field2")
        return keysArray?.optString(0)?.toFloatOrNull() ?: 0f
    }

    /**
     * Function to map a JSON object into the specific data format Map<String, Float>
     * for Vertical and Horizontal Bar charts.
     *
     * @param dataObj The JSON data object to process.
     * @return map with String keys and Float values.
     **/
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

    /**
     * Function to get the values list of a specific key from "field2". Stacked Bar
     * and Area charts.
     *
     * @param field2 The JSON data object to process.
     * @param keyIndex the current index that will receive the values list.
     * @return Float values list.
     **/
    private fun getfield2ValueList(field2: JSONObject?, keyIndex: Int): List<Float> {
        val valueList = mutableListOf<Float>()
        var seriesIndex = 0

        if (field2 !== null) {
            while (true) {
                val array = field2.optJSONArray(seriesIndex.toString()) ?: break
                val seriesValue = array.optString(keyIndex).toFloatOrNull() ?: 0f
                valueList.add(seriesValue)
                seriesIndex++
            }
        }

        return valueList
    }

    /**
     * Function to map a JSON object into the specific data format Map<String, List<Float>>
     * for Stacked Bar charts.
     *
     * @param dataObj The JSON data object to process.
     * @return map with String keys and List<Float> values.
     **/
    private fun parseToMapStringListFloat(dataObj: JSONObject?): Map<String, List<Float>> {
        val map = mutableMapOf<String, List<Float>>()
        if (dataObj == null) return map

        val keysArray = dataObj.optJSONArray("field1") ?: return map
        val field2 = dataObj.optJSONObject("field2")

        for (i in 0 until keysArray.length()) {
            val key = keysArray.optString(i)
            val valueList = getfield2ValueList(field2, i)
            map[key] = valueList
        }

        return map
    }

    /**
     * Function to map a JSON object into the specific data format Map<Float, Float>
     * for Line and Scatter charts.
     *
     * @param dataObj The JSON data object to process.
     * @return map of Float keys and Float values.
     **/
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

    /**
     * Function to map a JSON object into the specific data format Map<Float, List<Float>>
     * for Area charts.
     *
     * @param dataObj The JSON data object to process.
     * @return map with Float keys and List<Float> values.
     **/
    private fun parseToMapFloatListFloat(dataObj: JSONObject?): Map<Float, List<Float>> {
        val map = mutableMapOf<Float, List<Float>>()
        if (dataObj == null) return map

        val keysArray = dataObj.optJSONArray("field1") ?: return map
        val field2 = dataObj.optJSONObject("field2")

        for (i in 0 until keysArray.length()) {
            val key = keysArray.optString(i).toFloatOrNull() ?: 0f
            val valueList = getfield2ValueList(field2, i)
            map[key] = valueList
        }

        return map
    }

    /**
     * Function to map a JSON object into the specific data format List<Float>
     * for Pie and Donut charts.
     *
     * @param dataObj The JSON data object to process.
     * @return list of Float values.
     **/
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
