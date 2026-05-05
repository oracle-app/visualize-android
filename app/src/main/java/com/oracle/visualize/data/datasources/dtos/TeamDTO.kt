package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId

/**
 * Data Transfer Object representing a team in the database.
 *
 * @property id The unique identifier for the team document.
 * @property name The name of the team.
 * @property ownerID The unique ID of the user who owns the team.
 * @property membersIDs List of user IDs who are members of this team.
 */
data class TeamDTO(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val ownerID: String = "",
    val membersIDs: List<String> = listOf()
)
