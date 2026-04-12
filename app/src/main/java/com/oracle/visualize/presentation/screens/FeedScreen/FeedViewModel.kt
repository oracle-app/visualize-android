package com.oracle.visualize.presentation.screens.FeedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.FeedItem

class FeedViewModel : ViewModel() {

    private val allItems = listOf(
        FeedItem("GOTY (Graph Of The Year)", "Felipe Bastidas", "30 min ago"),
        FeedItem("Relative performance of major currencies against the dollar", "Eduardo Cardenas V.", "30 min ago")
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
                it.author.contains(newText, ignoreCase = true)
            }
        }
    }
}