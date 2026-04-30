package com.oracle.visualize.presentation.screens.teamsScreen

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

object TeamsMockData {
    val users = listOf(
        ShareUser(
            id = "user1",
            username = "Diana Escalante",
            email = "dianaescalante@gmail.com",
            profilePictureURL = "https://randomuser.me/api/portraits/women/1.jpg"
        ),
        ShareUser(
            id = "user2",
            username = "Jocelyn Duarte",
            email = "jocelynduarte@gmail.com",
            profilePictureURL = "https://randomuser.me/api/portraits/women/2.jpg"
        ),
        ShareUser(
            id = "user3",
            username = "Eduardo Salazar",
            email = "eduardosalazar@gmail.com",
            profilePictureURL = "https://randomuser.me/api/portraits/men/3.jpg"
        ),
        ShareUser(
            id = "user4",
            username = "Mariana Ruiz",
            email = "marianaruiz@gmail.com",
            profilePictureURL = "https://randomuser.me/api/portraits/women/4.jpg"
        ),
        ShareUser(
            id = "user5",
            username = "Mariana Islas",
            email = "marianaislas@gmail.com",
            profilePictureURL = "https://randomuser.me/api/portraits/women/5.jpg"
        ),
        ShareUser(
            id = "user6",
            username = "Lucy Martinez",
            email = "lucymartinez@gmail.com",
            profilePictureURL = "https://randomuser.me/api/portraits/women/6.jpg"
        )
    )

    val myTeams = mutableListOf(
        ShareTeam(
            id = "team1",
            name = "Data Analyst",
            memberCount = 2,
            members = listOf(users[0], users[1])
        ),
        ShareTeam(
            id = "team2",
            name = "Data Analyst",
            memberCount = 5,
            members = users.take(5)
        )
    )

    val teamsImIn = mutableListOf(
        ShareTeam(
            id = "team3",
            name = "Data Analyst",
            memberCount = 5,
            members = users.take(5)
        ),
        ShareTeam(
            id = "team4",
            name = "Data Analyst",
            memberCount = 5,
            members = users.take(5)
        ),
        ShareTeam(
            id = "team5",
            name = "Data Analyst",
            memberCount = 5,
            members = users.take(5)
        ),
        ShareTeam(
            id = "team6",
            name = "Data Analyst",
            memberCount = 5,
            members = users.take(5)
        ),
        ShareTeam(
            id = "team7",
            name = "Data Analyst",
            memberCount = 5,
            members = users.take(5)
        )
    )

    fun addTeam(name: String, members: List<ShareUser>) {
        val newTeam = ShareTeam(
            id = "team_${System.currentTimeMillis()}",
            name = name,
            memberCount = members.size,
            members = members
        )
        myTeams.add(0, newTeam)
    }
}
