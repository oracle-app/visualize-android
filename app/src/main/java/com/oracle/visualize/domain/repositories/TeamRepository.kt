package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareTeam

interface TeamRepository {

    suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam>

    suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam>

}