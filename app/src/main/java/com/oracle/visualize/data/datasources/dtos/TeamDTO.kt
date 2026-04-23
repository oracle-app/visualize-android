package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId

data class TeamDTO(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val ownerID: String = "",
    val membersIDs: List<String> = listOf()
)
