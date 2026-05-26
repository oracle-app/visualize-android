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
        if (previewJson?.isBlank() == true || previewJson == "{}") return null

        return try {
            val jsonObject = JSONObject(previewJson)
            val chartName = jsonObject.optString("chartName", "No Title")
            val chartTypeStr = jsonObject.optString("chartType", "")

            // Metrics' field names.
            val metricsObj = jsonObject.optJSONObject("metrics")
            val metricsNames = mutableListOf<String>()
            metricsObj?.let {
                val keys = it.keys()
                while (keys.hasNext()) {
                    metricsNames.add(it.getString(keys.next()))
                }
            }

            val dataObj = jsonObject.optJSONObject("data")

            /*
            * Get field names from "field1", applying to:
            * Vertical Bar, Horizontal Bar, Pie, Donut, and Area charts
            * */
            val field1FieldNames = getField1Categories(dataObj)

            when (chartTypeStr) {
                "Vertical Bar Chart" -> {
                    VerticalBarChart(chartName, parseToMapStringFloat(dataObj), metricsNames, field1FieldNames)
                }
                "Horizontal Bar Chart" -> {
                    HorizontalBarChart(chartName, parseToMapStringFloat(dataObj), metricsNames, field1FieldNames)
                }
                "Stacked Bar Chart" -> {
                    val stackNames = getStackedBarStackNames(dataObj, metricsObj, metricsNames)
                    StackedBarChart(chartName, parseToMapStringListFloat(dataObj), metricsNames, stackNames)
                }
                "Line Chart", "Line" -> {
                    LineChart(chartName, parseToMapFloatFloat(dataObj), metricsNames, metricsNames)
                }
                "Pie Chart", "Pie" -> {
                    PieChartModel(chartName, parseToListFloat(dataObj), metricsNames, field1FieldNames)
                }
                "Donut Chart", "Donut" -> {
                    DonutChart(chartName, parseToListFloat(dataObj), metricsNames, field1FieldNames)
                }
                "Scatter Chart", "Scatter" -> {
                    ScatterChart(chartName, parseToMapFloatFloat(dataObj), metricsNames, metricsNames)
                }
                "Area Chart", "Area" -> {
                    AreaChart(chartName, parseToMapFloatListFloat(dataObj), metricsNames, field1FieldNames)
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
     * Function to get stack names for Stack Bar charts.
     *
     * @param dataObj The JSON data object to process.
     * @param metricsObj The JSON metric object to process.
     * @param fieldNames The field names list.
     * @return the stack names list.
     **/
    private fun getStackedBarStackNames(
        dataObj: JSONObject?, metricsObj: JSONObject?, fieldNames: List<String>
    ): List<String> {
        var stackedBarStackNames = if (metricsObj !== null) fieldNames.drop(1) else emptyList()

        if (dataObj != null && stackedBarStackNames.isEmpty() ) {
            val field2 = dataObj.optJSONObject("field2")
            if (field2 !== null) {
                val f2Keys = field2.keys()
                val f2KeysList = mutableListOf<String>()
                while (f2Keys.hasNext()) {
                    f2KeysList.add(f2Keys.next())
                }
                stackedBarStackNames = f2KeysList
            }
        }

        return stackedBarStackNames
    }

    /**
     * Function to get field names for charts that have them on "field1" from the "data"
     * JSON object.
     *
     * @param dataObj The JSON data object to process.
     * @return the field names list.
     **/
    private fun getField1Categories(dataObj: JSONObject?): List<String> {
        val categories = mutableListOf<String>()
        val field1 = dataObj?.optJSONArray("field1")

        if (field1 != null) {
            for (i in 0 until field1.length()) {
                categories.add(field1.optString(i).ifBlank { "Cat ${i + 1}" })
            }
        }

        return categories
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
            val valueList = mutableListOf<Float>()
            var seriesIndex = 0

            if (field2 !== null) {
                while (true) {
                    val array = field2.optJSONArray(seriesIndex.toString()) ?: break
                    val seriesValue = array.optString(i).toFloatOrNull() ?: 0f
                    valueList.add(seriesValue)
                    seriesIndex++
                }
            }

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
        val field2 = dataObj.optJSONObject("field2") ?: return map
        val f2Keys = field2.keys()

        while (f2Keys.hasNext()) {
            val xValue = f2Keys.next()
            val xValueFloat = xValue.toFloatOrNull() ?: 0f
            val currentValArray = field2.optJSONArray(xValue)
            val processedYValues = mutableListOf<Float>()

            if (currentValArray != null) {
                for (i in 0..currentValArray.length()) {
                    processedYValues.add(currentValArray.optString(i).toFloatOrNull() ?: 0f)
                }
            }

            map[xValueFloat] = processedYValues
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

