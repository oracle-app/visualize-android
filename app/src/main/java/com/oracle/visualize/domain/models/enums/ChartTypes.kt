package com.oracle.visualize.domain.models.enums

enum class ChartTypes(val typeName: String) {
    VERTICAL_BAR("Vertical Bar Chart"),
    HORIZONTAL_BAR("Horizontal Bar Chart"),
    STACKED_BAR("Stacked Bar Chart"),
    LINE("Line"),
    PIE("Pie"),
    DONUT("Donut"),
    SCATTER("Scatter"),
    AREA("Area");

    companion object {
        fun fromTypeName(typeName: String): ChartTypes =
            entries.firstOrNull() { it.typeName == typeName } ?:
            throw IllegalArgumentException("Invalid chart type")
    }
}