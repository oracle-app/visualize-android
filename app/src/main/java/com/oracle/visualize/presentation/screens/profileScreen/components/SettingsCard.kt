package com.oracle.visualize.presentation.screens.profileScreen.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    titleDrawableImage: Int? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val strokeColor = MaterialTheme.colorScheme.outlineVariant
    val titleColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier.fillMaxWidth().drawBehind {
            val stroke = 1.dp.toPx()
            val y = stroke / 2

            drawLine(
                color = strokeColor,
                start = Offset(0f, y), end = Offset(size.width, y),
                strokeWidth = stroke
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (titleDrawableImage != null) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp), tint = titleColor,
                        painter = painterResource(titleDrawableImage), contentDescription = title,
                    )
                }

                Text(
                    text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = titleColor,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            content()
        }
    }
}

