package com.oracle.visualize.presentation.screens.createEditScreen

import com.oracle.visualize.domain.models.ShareUser

sealed interface CreateEditTeamUiEvent {
    data class NameChanged(val name: String) : CreateEditTeamUiEvent
    data class SearchQueryChanged(val query: String) : CreateEditTeamUiEvent
    data class AddMember(val user: ShareUser) : CreateEditTeamUiEvent
    data class RemoveMember(val user: ShareUser) : CreateEditTeamUiEvent
    object Submit : CreateEditTeamUiEvent
}