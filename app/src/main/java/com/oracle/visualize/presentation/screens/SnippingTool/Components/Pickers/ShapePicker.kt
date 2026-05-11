package com.oracle.visualize.presentation.screens.SnippingTool.Components.Pickers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.oracle.visualize.presentation.screens.SnippingTool.Components.ShapeType

@Composable
fun ShapePicker(
    onShapeChange: (ShapeType) -> Unit,
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
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShapeType.entries.forEach { shape ->
                    IconButton(
                        onClick = { onShapeChange(shape) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = when (shape) {
                                ShapeType.CIRCLE -> Icons.Outlined.Circle
                                ShapeType.RECTANGLE -> Icons.Outlined.Rectangle
                                ShapeType.LINE -> Icons.Default.Remove
                                ShapeType.TRIANGLE -> Icons.Outlined.ChangeHistory
                            },
                            contentDescription = shape.name,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
