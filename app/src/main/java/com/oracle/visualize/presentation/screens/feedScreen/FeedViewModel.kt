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
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase
) : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf(VisualizationFilter.ALL)
        private set

    var items by mutableStateOf<List<VisualizationCard>>(emptyList())
        private set

    init {
        fetchItems(VisualizationFilter.ALL)
    }

    private fun fetchItems(filter: VisualizationFilter) {
        viewModelScope.launch {
            try {
                val userID = "Aldo Ruiz"
                items = getAllUserVisualizationsUseCase.invoke(userID, filter)
            } catch (ex: FirebaseFirestoreException) {
                items = emptyList()
                Log.e("Error", "Couldn't load the visualizations.")
            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        selectedFilter = filter
        searchText = ""
        fetchItems(filter)
    }

    fun onSearchTextChange(newText: String) {
        searchText = newText
    }
}