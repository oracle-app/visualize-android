package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.CommentDto
import com.oracle.visualize.domain.models.Comment

fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    authorID = authorID,
    content = content,
    createdAt = createdAt,
    imageUrl = imageUrl,
    threads = threads.map { it.toDomain() }
)