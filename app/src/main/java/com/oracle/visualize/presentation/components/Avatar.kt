package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.oracle.visualize.R

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    userName: String,
    profileImageUrl: Any
) {
    val initial = userName.firstOrNull()?.uppercase() ?: "?"

    SubcomposeAsyncImage(
        model = profileImageUrl,
        contentDescription = stringResource(R.string.profile_img_description),
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Crop,
        loading = {
            FallbackAvatar(initial = initial)
        },
        error = {
            FallbackAvatar(initial = initial)
        }
    )
}

@Composable
private fun FallbackAvatar(initial: String) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = (maxWidth * 0.45f).value.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
