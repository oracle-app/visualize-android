package com.oracle.visualize.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Renders a tootlip based on the provided chart [data].
 *
 * @param data The list of properties to display (ex.: "x: 10.0", "y: 20.0").
 */
@Composable
fun ChartTooltip(data: List<String>) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(8.dp)
        ) {
            for (i in data) {
                Text(text = i, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
