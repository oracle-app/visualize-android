package com.oracle.visualize.data.repositories

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.oracle.visualize.domain.models.Dataset
import com.oracle.visualize.domain.repositories.DatasetRepository
import java.util.Locale

class DatasetRepositoryImpl(private val context: Context) : DatasetRepository {
    
    override fun getDatasetInfo(uri: Uri): Dataset? {
        var fileName = "unknown_file"
        var fileSize = "0 MB"
        
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
                
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    val sizeBytes = it.getLong(sizeIndex)
                    val mbSize = sizeBytes / (1024f * 1024f)
                    fileSize = String.format(Locale.ROOT, "%.1f MB", mbSize)
                }
            }
        }
        
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.ROOT)
        
        return Dataset(fileName, fileSize, extension)
    }
}
