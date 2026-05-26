package com.oracle.visualize.data.mapper

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.datasources.dtos.ThreadDTO
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread

fun CommentDTO.toDomain(
    threads: List<Thread> = emptyList()
): Comment = Comment(
    id = id,
    authorID = authorID,
    content = content,
    createdAt = createdAt.toDate(),
    imageURL = imageURL,
    threads = threads
)

fun Comment.toDTO(): CommentDTO = CommentDTO(
    id = id,
    authorID = authorID,
    content = content,
    createdAt = Timestamp(createdAt),
    imageURL = imageURL
)

fun ThreadDTO.toDomain(): Thread = Thread(
    id = id,
    authorID = authorID,
    authorName = authorName,
    authorAvatarURL = authorAvatarURL,
    content = content,
    createdAt = createdAt.toDate()
)

fun Thread.toDTO(): ThreadDTO = ThreadDTO(
    id = id,
    authorID = authorID,
    authorName = authorName,
    authorAvatarURL = authorAvatarURL,
    content = content,
    createdAt = Timestamp(createdAt)
)
