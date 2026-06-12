package com.oracle.visualize.domain.models.policyObjects

import com.oracle.visualize.domain.models.enums.UserType

data class VisualizationPermissions(
    val canDelete: Boolean,
    val canHide: Boolean,
    val canShare: Boolean
) {
    companion object {
        operator fun invoke(
            userType: UserType,
            currentUserID: String,
            authorID: String
        ): VisualizationPermissions {
            val isOwner = currentUserID == authorID

            return when (userType) {
                UserType.ADMIN -> VisualizationPermissions(
                    canDelete = true,
                    canHide = !isOwner,
                    canShare = true
                )
                UserType.WRITER -> VisualizationPermissions(
                    canDelete = isOwner,
                    canHide = !isOwner,
                    canShare = isOwner
                )
                UserType.CONSUMER -> VisualizationPermissions(
                    canDelete = false,
                    canHide = true,
                    canShare = false
                )
            }
        }
    }
}
