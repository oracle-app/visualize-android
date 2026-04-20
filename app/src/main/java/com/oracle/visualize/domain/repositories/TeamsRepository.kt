package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareTeam

interface TeamsRepository {

    fun getTeamsOwnedByUser(userID: String): List<ShareTeam>

    fun getTeamsUserIsIn(userID: String): List<ShareTeam>

}