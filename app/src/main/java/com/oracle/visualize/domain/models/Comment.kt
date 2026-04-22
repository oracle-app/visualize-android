package com.oracle.visualize.domain.models

import java.util.Date

data class Comment (
    val id: String,
    val authorID: String,
    val content: String,
    val createdAt: Date,
    val imageUrl: String,
    val threads: List<Thread>
)