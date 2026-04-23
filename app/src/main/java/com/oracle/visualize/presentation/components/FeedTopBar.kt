package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.enums.VisualizationFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
    onFilterSelected: (VisualizationFilter) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val filterLabels = mapOf(
        VisualizationFilter.ALL      to "All Feed",
        VisualizationFilter.PERSONAL to "Personal Feed",
        VisualizationFilter.SHARED   to "Shared Feed"
    )

    TopAppBar(
        title = {
            Text(
                text = filterLabels[selectedFilter] ?: stringResource(R.string.feed_top_bar_title),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        },
        actions = {
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.feed_top_bar_options_description)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(200.dp).background(Color.White) // ancho fijo como en el screenshot
            ) {
                VisualizationFilter.entries.forEach { filter ->
                    val isSelected = filter == selectedFilter

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = filterLabels[filter] ?: filter.name,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            onFilterSelected(filter)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .background(
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else
                                    Color.White
                            )
                            .clip(RoundedCornerShape(10.dp)),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}