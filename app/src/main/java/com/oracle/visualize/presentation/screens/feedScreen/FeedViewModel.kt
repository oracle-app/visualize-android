package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.Visualization
import kotlinx.serialization.json.JsonObject
import java.util.Date

class FeedViewModel : ViewModel() {

    private val allItems = listOf(
        Visualization(
            "1", "Felipe Bastidas",
            "GOTY (Graph Of The Year)",
            JsonObject(emptyMap()),
            emptyList(),
            emptyList(),
            2,
            true,
            Date(System.currentTimeMillis() - 30 * 60 * 1000)
        ),
        Visualization(
            "2", "Eduardo Cardenas",
            "Relative performance of major currencies",
            JsonObject(emptyMap()),
            emptyList(),
            emptyList(),
            7,
            true,
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        ),
        Visualization(
            "3", "Eduardo Cardenas",
            "Relative performance of major currencies",
            JsonObject(emptyMap()),
            emptyList(),
            emptyList(),
            7,
            true,
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        ),
        Visualization(
            "4", "Eduardo Cardenas",
            "Relative performance of major currencies",
            JsonObject(emptyMap()),
            emptyList(),
            emptyList(),
            7,
            true,
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        )
    )

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