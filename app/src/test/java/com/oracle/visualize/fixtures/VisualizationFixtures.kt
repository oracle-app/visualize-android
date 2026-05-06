package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.VisualizationCard
import java.util.Date

object VisualizationFixtures {

    const val VALID_USER_ID = "user123"

    val fakeVisualizationCard = VisualizationCard(
        id = "1",
        title = "Chart A",
        author = "John",
        createdAt = Date(),
        teamsSharedWith = emptyList(),
        usersSharedWith = emptyList(),
        allUsersSharedWith = emptyList(),
        configJSON = "{}"
    )

    val fakeVisualizations = listOf(
        fakeVisualizationCard,
        fakeVisualizationCard.copy(id = "2", title = "Chart B")
    )

}