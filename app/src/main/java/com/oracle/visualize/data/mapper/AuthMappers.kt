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
