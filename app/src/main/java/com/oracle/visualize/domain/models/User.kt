package com.oracle.visualize.domain.models

data class User (
    val id: String,
    val email: String,
    val username: String,
    val profilePictureURL: String?,
)