package com.oracle.visualize.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.models.Visualization

fun FirebaseUser.toDomain(): AuthUser = AuthUser(
    uid = uid,
    email = email ?: ""
)

fun Visualization.toDomain(): Visualization = Visualization(
    id = id,
    authorID = authorID,
    title = title,
    configJSON = configJSON,
    sharedWithUsers = sharedWithUsers,
    sharedWithTeams = sharedWithTeams,
    createdAt = createdAt,
    comments = comments
)