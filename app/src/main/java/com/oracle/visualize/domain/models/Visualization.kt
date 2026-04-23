package com.oracle.visualize.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Visualization (
    @DocumentId
    val id: String,
    val authorID: String,
    val title: String,
    val configJSON: String,
    val sharedWithUsers: List<String>,
    val sharedWithTeams: List<String>,
    val createdAt: Timestamp,
)