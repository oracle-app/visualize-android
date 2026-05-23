package com.oracle.visualize.data.datasources.local

import android.util.LruCache
import com.oracle.visualize.domain.models.Chart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartCacheManager @Inject constructor() {
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
