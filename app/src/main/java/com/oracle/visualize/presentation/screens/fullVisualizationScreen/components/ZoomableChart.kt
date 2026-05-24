package com.oracle.visualize.presentation.screens.fullVisualizationScreen.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.DonutChart
import com.oracle.visualize.domain.models.PieChartModel

@Composable
fun ZoomableChart(
    chart: Chart<*>,
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 12f,
    content: @Composable BoxScope.() -> Unit
) {
    when (chart) {
        is PieChartModel, is DonutChart -> {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            var containerSpaceSize by remember { mutableStateOf(Size.Zero) }

            Box(
                modifier = modifier
                    .clipToBounds()
                    .onGloballyPositioned { containerSpaceSize = it.size.toSize() }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                            val containerXMax = containerSpaceSize.width * (newScale - 1) * 0.5f
                            val containerYMax = containerSpaceSize.height * (newScale - 1) * 0.5f

                            val updatedOffset = if (newScale == minScale) {
                                Offset.Zero
                            } else {
                                val offsetPan = offset + pan

                                Offset(
                                    x = offsetPan.x.coerceIn(-containerXMax, containerXMax),
                                    y = offsetPan.y.coerceIn(-containerYMax, containerYMax)
                                )
                            }

                            offset = updatedOffset
                            scale = newScale
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = if (scale == minScale) 2f else minScale
                                offset = Offset.Zero
                            }
                        )
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                content()
            }
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize()) { content() }
        }
    }
}
