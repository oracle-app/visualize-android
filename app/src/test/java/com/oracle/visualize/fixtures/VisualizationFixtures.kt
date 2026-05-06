package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.Visualization
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

    val fakeValidVisualization = Visualization(
        id = "1",
        title = "Chart A",
        authorID = "John",
        createdAt = Date(),
        sharedWithUsers = emptyList(),
        sharedWithTeams = emptyList(),
        configJSON = "{}"
    )

    val fakeVisualizations = listOf(
        fakeVisualizationCard,
        fakeVisualizationCard.copy(id = "2", title = "Chart B")
    )

    val visListWhereAllAreValid = listOf(
        fakeValidVisualization,
        fakeValidVisualization.copy(
            id="2", title = "Vis 2", authorID = "2", sharedWithUsers = listOf("1")),
    )

    val visListWhereOneHasEmptyTitle = listOf(
        fakeValidVisualization,
        fakeValidVisualization.copy(
            id="2", title = "", authorID = "2", sharedWithUsers = listOf("1")
        ),
    )

    val visListWhereOneHasEmptyAuthorID = listOf(
        fakeValidVisualization,
        fakeValidVisualization.copy(
            id="2", title = "Vis 2", authorID = "", sharedWithUsers = listOf("1")
        ),
    )

    val visListWhereOneHasEmptyConfigJSON = listOf(
        fakeValidVisualization,
        fakeValidVisualization.copy(
            id="2", title = "Vis 2", authorID = "2", configJSON = "",
            sharedWithUsers = listOf("1")
        ),
    )

    val visListWhereSomeAreValidAndSomeInvalid = listOf(
        fakeValidVisualization,
        fakeValidVisualization.copy(
            id="2", title = "Vis 2", authorID = "2", configJSON = "{}",
            sharedWithUsers = listOf("1")
        ),
        fakeValidVisualization.copy(
            id="3", title = "", authorID = "", configJSON = "",
            sharedWithUsers = listOf("1")
        ),
        fakeValidVisualization.copy(
            id="4", title = "sdasdasd", authorID = "", configJSON = "",
            sharedWithTeams = listOf("2", "3")
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