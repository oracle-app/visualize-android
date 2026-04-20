package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentReference

data class TeamDTO(
    val id: String,
    val memberCount: Int,
    val name: String,
    val ownerID: DocumentReference,
    val membersIDs: List<DocumentReference>
)
