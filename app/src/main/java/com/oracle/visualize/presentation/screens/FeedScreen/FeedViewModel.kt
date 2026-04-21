package com.oracle.visualize.presentation.screens.FeedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import java.util.Date

class FeedViewModel : ViewModel() {
    private val repo = VisualizationRepository()

    /*
    * Some examples to test:
    *
    * private val allItems = repo.getGeneralVisualizationsByUser("Jorge Ruiz")
    * private val allItems = repo.getPersonalVisualizations("Felipe Bastidas")
    * */

    private val allItems = repo.getAllVisualizations()

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
                it.ownerId.contains(newText, ignoreCase = true)
            }
        }
    }
}