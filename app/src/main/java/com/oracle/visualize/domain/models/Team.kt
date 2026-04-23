package com.oracle.visualize.domain.models

data class Team (
    val id: String,
    val memberIDs: List<String>,
    val name: String,
    val ownerID: String
)