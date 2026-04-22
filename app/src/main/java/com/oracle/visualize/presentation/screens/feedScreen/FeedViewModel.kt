package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.usecases.CreateVisualizationUseCase
import com.oracle.visualize.domain.usecases.GetAllVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetPersonalVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetSharedVisualizationsByUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import java.util.Date
import kotlin.collections.emptyList

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val createVisualizationUseCase: CreateVisualizationUseCase,
    private val getAllVisualizationsUseCase: GetAllVisualizationsUseCase,
    private val getSharedVisualizationsByUser: GetSharedVisualizationsByUserUseCase
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
                visualizations = getAllVisualizationsUseCase()
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