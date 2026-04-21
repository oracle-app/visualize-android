package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentReference

data class TeamDTO(
    val id: String,
    val name: String,
    val ownerID: String,
    val membersIDs: List<String>
)
