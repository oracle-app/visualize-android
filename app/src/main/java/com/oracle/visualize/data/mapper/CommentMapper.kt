package com.oracle.visualize.data.mapper

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.datasources.dtos.ThreadDTO
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread

fun CommentDTO.toDomain(): Comment = Comment(
    id = id,
    authorId = authorId,
    authorName = authorName,
    authorImageUrl = authorImageUrl,
    content = content,
    imageUrl = imageUrl,
    createdAt = createdAt.toDate()
)

fun Comment.toDTO(): CommentDTO = CommentDTO(
    id = id,
    authorId = authorId,
    authorName = authorName,
    authorImageUrl = authorImageUrl,
    content = content,
    imageUrl = imageUrl,
    createdAt = Timestamp(createdAt)
)

fun ThreadDTO.toDomain(): Thread = Thread(
    id = id,
    authorId = authorId,
    authorName = authorName,
    authorImageUrl = authorImageUrl,
    content = content,
    imageUrl = imageUrl,
    createdAt = createdAt.toDate()
)

fun Thread.toDTO(): ThreadDTO = ThreadDTO(
    id = id,
    authorId = authorId,
    authorName = authorName,
    authorImageUrl = authorImageUrl,
    content = content,
    imageUrl = imageUrl,
    createdAt = Timestamp(createdAt)
)
