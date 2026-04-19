package com.oracle.visualize.domain.models

data class ShareTeam(
    val id: String,
    val name: String,
    val memberCount: Int,
    val members: List<ShareUser> = emptyList()
    // Note: isSelected was removed — selection is UI state,
    // not a property of the business entity.
)