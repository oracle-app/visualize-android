package com.oracle.visualize.domain.usecases

import android.util.Log
import com.oracle.visualize.data.datasources.local.FeedCacheManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearFeedCacheUseCase @Inject constructor(
    private val feedCacheManager: FeedCacheManager
) {
    operator fun invoke() {
        feedCacheManager.clearCache()
    }
}
