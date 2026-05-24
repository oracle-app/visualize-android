package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import java.util.Date

object VisualizationFixtures {

    const val VALID_USER_ID = "user123"
    const val VALID_VISUALIZATION_ID = "1"

    val fakeVisualizationCard = VisualizationCard(
        id = VALID_VISUALIZATION_ID,
        title = "Chart A",
        author = "John",
        authorID = "2",
        createdAt = Date(),
        teamsSharedWith = emptyList(),
        usersSharedWith = emptyList(),
        allUsersSharedWith = emptyList()
    )

    val fakeValidVisualization = Visualization(
        id = "1",
        title = "Chart A",
        authorID = "John",
        createdAt = Date(),
        sharedWithUsers = emptyList(),
        sharedWithTeams = emptyList(),
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

    val visListWhereAllAreValid = listOf(
        fakeValidVisualization,
        fakeValidVisualization.copy(
            id="2", title = "Vis 2", authorID = "2", sharedWithUsers = listOf("1")
        ),
        fakeValidVisualization.copy(
            id="3", title = "Vis 3", authorID = "2", sharedWithUsers = listOf("1", "2")
        ),
    )

    val visListWhereAllAreInvalid = listOf(
        fakeValidVisualization.copy(title = ""),
        fakeValidVisualization.copy(
            id="2", title = "Vis 2", authorID = "2", configJSON = "",
            sharedWithUsers = listOf("1")
        ),
    )
}
