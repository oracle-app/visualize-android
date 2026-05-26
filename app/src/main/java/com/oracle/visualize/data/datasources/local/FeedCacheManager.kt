package com.oracle.visualize.data.datasources.local

import com.oracle.visualize.domain.models.VisualizationCard
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedCacheManager @Inject constructor() {
    var cachedFeed: List<VisualizationCard>? = null

    fun clearCache() {
        cachedFeed = null
    }
}
