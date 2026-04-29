package com.oracle.visualize.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.oracle.visualize.domain.models.AuthUser

/**
 * Extension function to map [FirebaseUser] from Firebase Auth to [AuthUser] domain model.
 *
 * @return An [AuthUser] object containing the UID and email.
 */
fun FirebaseUser.toDomain(): AuthUser = AuthUser(
    uid = uid,
    email = email ?: ""
)
