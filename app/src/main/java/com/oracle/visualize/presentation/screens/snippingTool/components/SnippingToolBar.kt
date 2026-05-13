package com.oracle.visualize.presentation.screens.snippingTool.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oracle.visualize.ui.theme.DrawModeColors
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.oracle.visualize.presentation.screens.snippingTool.components.pickers.ColorPicker
import com.oracle.visualize.presentation.screens.snippingTool.components.pickers.ShapePicker
import com.oracle.visualize.presentation.screens.snippingTool.components.pickers.ThicknessPicker
import androidx.compose.ui.res.stringResource
import com.oracle.visualize.R

@Composable
fun SnippingToolbar(
    onPenClick: () -> Unit,
    onEraserClick: () -> Unit,
    onColorClick: (DrawModeColors) -> Unit,
    strokeWidth: Float,
    onThicknessClick: (Float) -> Unit,
    onTextClick: () -> Unit,
    onShapeClick: (ShapeType) -> Unit,
    onCropClick: () -> Unit,
    selectedColor: Color,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showThicknessPicker by remember { mutableStateOf(false) }
    var showShapePicker by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val offsetPx = with(density) { 64.dp.roundToPx() }

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPenClick, modifier = Modifier.size(32.dp)) {
                    Icon(painter = painterResource(R.drawable.pen), contentDescription = stringResource(R.string.toolbar_pen), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                IconButton(onClick = onEraserClick, modifier = Modifier.size(32.dp)) {
                    Icon(painter = painterResource(R.drawable.eraser), contentDescription = stringResource(R.string.toolbar_eraser), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Box {
                    IconButton(onClick = { showColorPicker = !showColorPicker }, modifier = Modifier.size(32.dp)) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(selectedColor)
                                .border(2.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(4.dp))
                        )
                    }
                    if (showColorPicker) {
                        Popup(
                            alignment = Alignment.BottomCenter,
                            offset = IntOffset(0, -offsetPx)
                        ) {
                            ColorPicker(
                                selectedColor = selectedColor,
                                onColorChange = { color ->
                                    onColorClick(color)
                                    showColorPicker = false
                                }
                            )
                        }
                    }
                }
                Box {
                    IconButton(onClick = { showThicknessPicker = !showThicknessPicker }, modifier = Modifier.size(32.dp)) {
                        Icon(painter = painterResource(R.drawable.thickness), contentDescription = stringResource(R.string.toolbar_thickness), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    if (showThicknessPicker) {
                        Popup(
                            alignment = Alignment.BottomCenter,
                            offset = IntOffset(0, -offsetPx)
                        ) {
                            ThicknessPicker(
                                strokeWidth = strokeWidth,
                                onThicknessChange = {
                                    onThicknessClick(it)
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = onTextClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.TextFields, contentDescription = stringResource(R.string.toolbar_text), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Box {
                    IconButton(onClick = { showShapePicker = !showShapePicker }, modifier = Modifier.size(32.dp)) {
                        Icon(painter = painterResource(R.drawable.shapes), contentDescription = stringResource(R.string.toolbar_shape), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    if (showShapePicker) {
                        Popup(
                            alignment = Alignment.BottomCenter,
                            offset = IntOffset(0, -offsetPx)
                        ) {
                            ShapePicker(
                                onShapeChange = { shape ->
                                    onShapeClick(shape)
                                    showShapePicker = false
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    showColorPicker = false
                    showThicknessPicker = false
                    showShapePicker = false
                    onCropClick()
                }, modifier = Modifier.size(32.dp)) {
                    Icon(painter = painterResource(R.drawable.scissors), contentDescription = stringResource(R.string.toolbar_crop), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}
