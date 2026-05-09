package com.oracle.visualize.presentation.screens.threadsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage

@Composable
fun ThreadAvatar(
    username: String,
    profilePictureUrl: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {

    val initial = username.firstOrNull()?.uppercase() ?: "?"

    if (!profilePictureUrl.isNullOrBlank()) {

        SubcomposeAsyncImage(
            model = profilePictureUrl,
            contentDescription = username,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            loading = {
                FallbackAvatar(
                    initial = initial,
                    size = size
                )
            },
            error = {
                FallbackAvatar(
                    initial = initial,
                    size = size
                )
            }
        )

    } else {

        FallbackAvatar(
            initial = initial,
            size = size,
            modifier = modifier
        )
    }
}

@Composable
private fun FallbackAvatar(
    initial: String,
    size: Dp,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = initial,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.38f).sp
        )
    }
}