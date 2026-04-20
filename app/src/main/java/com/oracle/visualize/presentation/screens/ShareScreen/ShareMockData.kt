package com.oracle.visualize.presentation.screens.ShareScreen

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

// TODO: Remove when Repository / UseCase layer is implemented.
// Mock data is kept here and passed INTO the ViewModel via loadData(),
// keeping the ViewModel ready to receive real data from any source.
object ShareMockData {

    val suggestedUsers = listOf(
        ShareUser("u_diana",   "Diana Escalante", "dianaescalante@gmail.com",  "D", 0xFFE8A87C),
        ShareUser("u_jocelyn", "Jocelyn Duarte",  "jocelynduarte@gmail.com",   "J", 0xFF7EC8C8),
        ShareUser("u_eduardo", "Eduardo Salazar", "eduardosalazar@gmail.com",  "E", 0xFF8CB87C)
    )

    val myTeams = listOf(
        ShareTeam(
            id = "team_data_1", name = "Data Analyst", memberCount = 2,
            members = listOf(
                ShareUser("u1", "Ana", "ana@gmail.com", "A", 0xFFE8A87C),
                ShareUser("u2", "Bob", "bob@gmail.com", "B", 0xFF7EC8C8)
            )
        ),
        ShareTeam(
            id = "team_data_2", name = "Data Analyst", memberCount = 5,
            members = listOf(
                ShareUser("u1", "Ana",  "ana@gmail.com",  "A", 0xFFE8A87C),
                ShareUser("u2", "Bob",  "bob@gmail.com",  "B", 0xFF7EC8C8),
                ShareUser("u3", "Carl", "carl@gmail.com", "C", 0xFFB8D4E8),
                ShareUser("u7", "Dana", "dana@gmail.com", "D", 0xFFE8C87C),
                ShareUser("u8", "Erik", "erik@gmail.com", "E", 0xFF8CB87C)
            )
        )
    )

    val teamsImIn = listOf(
        ShareTeam(
            id = "team_in_1", name = "Data Analyst", memberCount = 5,
            members = listOf(
                ShareUser("u4", "Diana", "diana@gmail.com", "D", 0xFFE8C87C),
                ShareUser("u5", "Eve",   "eve@gmail.com",   "E", 0xFF8CB87C),
                ShareUser("u9", "Frank", "frank@gmail.com", "F", 0xFFB87C8C)
            )
        ),
        ShareTeam(
            id = "team_in_2", name = "Data Analyst", memberCount = 5,
            members = listOf(
                ShareUser("u4",  "Diana", "diana@gmail.com", "D", 0xFFE8C87C),
                ShareUser("u5",  "Eve",   "eve@gmail.com",   "E", 0xFF8CB87C),
                ShareUser("u6",  "Frank", "frank@gmail.com", "F", 0xFFB87C8C),
                ShareUser("u10", "Gina",  "gina@gmail.com",  "G", 0xFF9CB8E8),
                ShareUser("u11", "Hank",  "hank@gmail.com",  "H", 0xFFE89CB8)
            )
        )
    )
}