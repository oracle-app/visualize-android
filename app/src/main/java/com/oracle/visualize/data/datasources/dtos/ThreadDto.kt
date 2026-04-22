package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ThreadDto (
    @DocumentId
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)