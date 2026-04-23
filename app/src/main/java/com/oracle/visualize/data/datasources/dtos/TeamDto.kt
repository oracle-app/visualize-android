package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId

class TeamDto {
    @DocumentId
    val id: String = ""
    val memberIDs: List<String> = emptyList()
    val name: String = ""
    val ownerID: String = ""
}