package com.oracle.visualize.presentation.screens.feedScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUIState())
    val uiState: StateFlow<FeedUIState> = _uiState.asStateFlow()

    private var allItems: List<VisualizationCard> = emptyList()

    init {
        fetchItems(VisualizationFilter.ALL)
    }

    private fun fetchItems(filter: VisualizationFilter) {
        viewModelScope.launch {
            // Ponemos la pantalla en modo carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val userID = "oEJtQz0gdbRpTZ8ETPCy"
                allItems = getAllUserVisualizationsUseCase.invoke(userID, filter)
                applySearch()
            } catch (ex: FirebaseFirestoreException) {
                allItems = emptyList()
                // Actualizamos el estado con el error
                _uiState.update {
                    it.copy(isLoading = false, items = emptyList(), errorMessage = "Couldn't load the visualizations.")
                }
                Log.e("Error", "Couldn't load the visualizations.")
            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        _uiState.update { it.copy(selectedFilter = filter, searchText = "") }
        fetchItems(filter)
    }

    fun onSearchTextChange(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
        applySearch()
    }

    private fun applySearch() {
        val currentSearch = _uiState.value.searchText
        val filteredItems = if (currentSearch.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                item.title.contains(currentSearch, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(items = filteredItems, isLoading = false) }
    }
}