package com.oracle.visualize.presentation.screens.FeedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.usecases.CreateVisualizationUseCase
import com.oracle.visualize.domain.usecases.GetAllVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetPersonalVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetSharedVisualizationsByUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val createVisualizationUseCase: CreateVisualizationUseCase,
    private val getAllVisualizationsUseCase: GetAllVisualizationsUseCase,
    private val getPersonalVisualizationsUseCase: GetPersonalVisualizationsUseCase,
    private val getSharedVisualizationsByUser: GetSharedVisualizationsByUserUseCase
) : ViewModel() {


    val allItems = emptyList<Visualization>()

    var searchText by mutableStateOf("")
        private set

    var items by mutableStateOf(allItems)
        private set

    fun onSearchTextChange(newText: String) {
        searchText = newText

        items = if (newText.isBlank()) {
            allItems
        } else {
            allItems.filter {
                it.title.contains(newText, ignoreCase = true) ||
                it.authorID.contains(newText, ignoreCase = true)
            }
        }
    }
}