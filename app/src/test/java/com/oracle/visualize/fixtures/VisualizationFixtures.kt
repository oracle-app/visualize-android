package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.VisualizationCard
import java.util.Date

object VisualizationFixtures {

    const val VALID_USER_ID = "user123"
    const val VALID_VISUALIZATION_ID = "vis123456"

    val fakeVisualizationCard = VisualizationCard(
        id = "1",
        title = "Chart A",
        author = "John",
        authorID = "2",
        createdAt = Date(),
        teamsSharedWith = emptyList(),
        usersSharedWith = emptyList(),
        allUsersSharedWith = emptyList(),
        configJSON = "{}"
    )

    val fakeSharedVisualizations = listOf(
        fakeVisualizationCard.copy(id = "shared_1", title = "Shared Chart A"),
        fakeVisualizationCard.copy(id = "shared_2", title = "Shared Chart B")
    )

    val fakePersonalVisualizations = listOf(
        fakeVisualizationCard.copy(id = "personal_1", title = "Personal Chart A"),
        fakeVisualizationCard.copy(id = "personal_2", title = "Personal Chart B")
    )

}