package com.oracle.visualize.presentation.screens.shareScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.oracle.visualize.domain.models.ShareUser

@Composable
fun UserAvatar(
    user: ShareUser,
    size: Int = 38
) {
    val initial = user.username.firstOrNull()?.uppercase() ?: "?"

    SubcomposeAsyncImage(
        model = user.profilePictureURL,
        contentDescription = "Avatar de ${user.username}",
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        loading = {
            FallbackAvatar(initial = initial, size = size)
        },
        error = {
            FallbackAvatar(initial = initial, size = size)
        }
    )
}

@Composable
private fun FallbackAvatar(
    initial: String,
    size: Int
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFFE8A87C)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = (size * 0.37f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}