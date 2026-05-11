package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.models.ShareUser

object UserFixtures {

    const val VALID_NAME = "Tester"
    const val VALID_EMAIL = "test@test.com"
    const val VALID_PASSWORD = "123456"
    const val VALID_UID = "user123"

    val fakeAuthUser = AuthUser(
        uid = VALID_UID,
        email = VALID_EMAIL
    )

    const val VALID_QUERY = "john"

    val fakeShareUsers = listOf(
        ShareUser(id = "1", username = "Claudia", email = "test@test.com", profilePictureURL = null),
        ShareUser(id = "2",username = "Joshua", email = "test2@test.com", profilePictureURL = "url"),
    )
}
