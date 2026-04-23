package com.oracle.visualize.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.oracle.visualize.data.datasources.dtos.CommentDto
import com.oracle.visualize.data.datasources.dtos.TeamDto
import com.oracle.visualize.data.datasources.dtos.ThreadDto
import com.oracle.visualize.data.datasources.dtos.UserDto
import com.oracle.visualize.data.datasources.dtos.VisualizationDto
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.Thread
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization

fun FirebaseUser.toDomain(): AuthUser = AuthUser(
    uid = uid,
    email = email ?: ""
)

fun VisualizationDto.toDomain(): Visualization = Visualization(
    id = id,
    authorID = authorID,
    title = title,
    configJSON = configJSON,
    sharedWithUsers = sharedWithUsers,
    sharedWithTeams = sharedWithTeams,
    createdAt = createdAt,
    comments = comments.map { it.toDomain() }
)

fun TeamDto.toDomain(): Team = Team(
    id = id,
    memberIDs = memberIDs,
    name = name,
    ownerID = ownerID
)

fun UserDto.toDomain(): User = User(
    id = id,
    userType = userType,
    email = email,
    userName = userName,
    profilePictureUrl = profilePictureUrl,
    themePreference = themePreference,
    chartTheme = chartTheme,
    notificationsEnabled = notificationsEnabled,
    hiddenVisualizations = hiddenVisualizations
)

fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    authorID = authorID,
    content = content,
    createdAt = createdAt,
    imageUrl = imageUrl,
    threads = threads.map { it.toDomain() }
)

fun ThreadDto.toDomain(): Thread = Thread(
    id = id,
    authorID = authorID,
    content = content,
    timestamp = timestamp
)
