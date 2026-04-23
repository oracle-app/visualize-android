package com.oracle.visualize.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.oracle.visualize.domain.models.AuthUser

fun FirebaseUser.toDomain(): AuthUser = AuthUser(
    uid = uid,
    email = email ?: ""
)
