package com.oracle.visualize.domain.models

data class ShareTeam(
    val id: String,
    val name: String,
    val memberCount: Int,
    val members: List<ShareUser> = emptyList()

)