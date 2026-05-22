package com.oracle.visualize.presentation.utils

import android.util.LruCache
import com.oracle.visualize.domain.models.Chart

object ChartCacheManager {
    private val cache = LruCache<String, Chart<*>>(50)

    fun getChart(id: String): Chart<*>? {
        return cache.get(id)
    }

    fun saveChart(id: String, chart: Chart<*>) {
        cache.put(id, chart)
    }

    fun clearCache() {
        cache.evictAll()
    }
}
