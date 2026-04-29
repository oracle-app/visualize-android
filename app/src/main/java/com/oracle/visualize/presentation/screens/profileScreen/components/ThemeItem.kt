package com.oracle.visualize.presentation.screens.profileScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oracle.visualize.ui.theme.ChartPalette

@Composable
fun ThemeItem(
    palette: ChartPalette,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var borderColor: Color
    val shape = RoundedCornerShape(35)

    if(isSelected.equals(true)) {
        borderColor = MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        borderColor = Color.Transparent
    }

    Row(
        modifier = modifier
            .height(20.dp)
            .border(2.dp, borderColor, shape)
            .clip(shape)
            .clickable { onClick() }
    ) {
        palette.colors.forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}