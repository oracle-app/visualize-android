package com.oracle.visualize.presentation.screens.snippingTool.components.pickers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R

@Composable
fun TextPicker(
    isItalics: Boolean,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    onItalicsToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(25),
            color = surfaceColor,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                IconButton(
                    onClick = {
                        onItalicsToggle(!isItalics)
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        Icons.Default.FormatItalic,
                        contentDescription = stringResource(R.string.toolbar_crop),
                        tint = if (isItalics) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = fontSize,
                        onValueChange = onFontSizeChange,
                        valueRange = 10f..50f,
                        modifier = Modifier
                            .width(200.dp)
                            .graphicsLayer { rotationZ = -90f }
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(
                                    Constraints.fixed(constraints.maxHeight, constraints.maxWidth)
                                )
                                layout(placeable.height, placeable.width) {
                                    placeable.place(-placeable.width / 2 + placeable.height / 2, -placeable.height / 2 + placeable.width / 2)
                                }
                            }
                    )
                }
            }
        }

        Canvas(modifier = Modifier.size(width = 16.dp, height = 16.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
                close()
            }
            drawPath(path, color = surfaceColor)
        }
    }
}
