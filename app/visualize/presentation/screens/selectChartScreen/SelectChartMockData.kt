package com.oracle.visualize.presentation.screens.selectChartScreen

<<<<<<< Updated upstream:app/src/main/java/com/oracle/visualize/presentation/screens/selectChartScreen/SelectChartMockData.kt
import com.oracle.visualize.domain.models.Visualization
import java.util.Date
=======
import com.google.firebase.Timestamp
import com.oracle.visualize.domain.models.Visualization
>>>>>>> Stashed changes:app/src/main/java/com/oracle/visualize/presentation/screens/selectChartScreen/selectChartMockData.kt
import java.util.UUID

/**
 * Mock data for development and testing purposes.
 */
<<<<<<< Updated upstream:app/src/main/java/com/oracle/visualize/presentation/screens/selectChartScreen/SelectChartMockData.kt
object SelectChartMockData {
    val visualizations = listOf(
        Visualization(
            id = UUID.randomUUID().toString(),
            authorID = "user1",
            title = "Commerce Activity: Units Sold vs Total Transactions",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        ),
        Visualization(
            id = UUID.randomUUID().toString(),
            authorID = "user1",
            title = "Units Sold vs Total Transactions",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        ),
        Visualization(
            id = UUID.randomUUID().toString(),
            authorID = "user1",
            title = "Commercial Performance Overview: Comparison Between Units Sold and Total Transaction Volume",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
=======
object selectChartMockData {
    val visualizations =
        listOf(
            Visualization(
                id = UUID.randomUUID().toString(),
                authorID = "user1",
                title = "Commerce Activity: Units Sold vs Total Transactions",
                configJSON = "{}",
                sharedWithUsers = emptyList(),
                sharedWithTeams = emptyList(),
                createdAt = Timestamp.now(),
            ),
            Visualization(
                id = UUID.randomUUID().toString(),
                authorID = "user1",
                title = "Units Sold vs Total Transactions",
                configJSON = "{}",
                sharedWithUsers = emptyList(),
                sharedWithTeams = emptyList(),
                createdAt = Timestamp.now(),
            ),
            Visualization(
                id = UUID.randomUUID().toString(),
                authorID = "user1",
                title = "Commercial Performance Overview: Comparison Between Units Sold and Total Transaction Volume",
                configJSON = "{}",
                sharedWithUsers = emptyList(),
                sharedWithTeams = emptyList(),
                createdAt = Timestamp.now(),
            ),
>>>>>>> Stashed changes:app/src/main/java/com/oracle/visualize/presentation/screens/selectChartScreen/selectChartMockData.kt
        )
}
