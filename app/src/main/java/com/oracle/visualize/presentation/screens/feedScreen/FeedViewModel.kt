package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.usecases.CreateVisualizationUseCase
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetAllVisualizationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val createVisualizationUseCase: CreateVisualizationUseCase,
    private val getAllVisualizationsUseCase: GetAllVisualizationsUseCase,
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase
) : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var visualizations by mutableStateOf<List<Visualization>>(emptyList())
        private set

    var items by mutableStateOf<List<Visualization>>(emptyList())
        private set

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch {
            try {
                /*
                // Fetch all visualizations from DB:
                val response = getAllVisualizationsUseCase()
                visualizations = response
                items = response
                */
                val userID = "Jose"     // Hard-coded User ID for testing.
                val response = getAllUserVisualizationsUseCase(userID)
                visualizations = response
                items = response
            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        searchText = newText

        items = if (newText.isBlank()) {
            visualizations
        } else {
            visualizations.filter {
                it.title.contains(newText, ignoreCase = true) ||
                it.authorID.contains(newText, ignoreCase = true)
            }
        }
    }
}