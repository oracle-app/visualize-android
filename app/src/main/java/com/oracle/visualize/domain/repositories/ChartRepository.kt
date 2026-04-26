package com.oracle.visualize.domain.repositories

import android.net.Uri
import com.oracle.visualize.domain.models.Chart

interface ChartRepository {
    suspend fun getCharts(file: Uri?): List<Chart>
}