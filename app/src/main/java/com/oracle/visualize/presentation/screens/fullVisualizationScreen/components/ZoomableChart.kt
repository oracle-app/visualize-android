package com.oracle.visualize.presentation.screens.fullVisualizationScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ZoomableChart(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier) { content() }
}
