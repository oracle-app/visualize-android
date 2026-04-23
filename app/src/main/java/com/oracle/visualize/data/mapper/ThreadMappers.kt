package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.ThreadDto
import com.oracle.visualize.domain.models.Thread

fun ThreadDto.toDomain(): Thread = Thread(
    id = id,
    authorID = authorID,
    content = content,
    timestamp = timestamp
)