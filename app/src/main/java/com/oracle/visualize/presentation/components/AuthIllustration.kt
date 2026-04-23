package com.oracle.visualize.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R

/**
 * Decorative orange illustration displayed at the top of auth screens.
 * Uses a vector drawable placeholder matching the Figma design.
 */
@Composable
fun AuthIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 140.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_auth_illustration),
        contentDescription = null, // decorative
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}
