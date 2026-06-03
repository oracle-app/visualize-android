package com.oracle.visualize.domain.models

import com.oracle.visualize.domain.models.enums.UserType

data class ThreadPermissions(
    val canDelete: Boolean
) {
    companion object {
        operator fun invoke(
            userType: UserType,
            currentUserID: String,
            visualizationOwnerID: String,
            commentAuthorID: String
        ): ThreadPermissions {
            val canDelete = when {
                userType == UserType.ADMIN -> true
                userType == UserType.WRITER && currentUserID == visualizationOwnerID -> true
                else -> currentUserID == commentAuthorID
            }

            return ThreadPermissions(canDelete = canDelete)
        }
    }
}
