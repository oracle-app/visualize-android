package com.oracle.visualize.domain.models

import java.util.Date

<<<<<<< Updated upstream
/**
 * Domain model representing a Visualization.
 */
data class Visualization (
=======
data class Visualization(
    @DocumentId
>>>>>>> Stashed changes
    val id: String,
    val authorID: String,
    val title: String,
    val configJSON: String,
    val sharedWithUsers: List<String>,
    val sharedWithTeams: List<String>,
<<<<<<< Updated upstream
    val createdAt: Date,
)
=======
    val createdAt: Timestamp,
)
>>>>>>> Stashed changes
