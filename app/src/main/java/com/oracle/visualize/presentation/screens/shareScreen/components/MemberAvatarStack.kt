package com.oracle.visualize.presentation.screens.shareScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.components.UserAvatar

private val AVATAR_SIZE = 29.dp
private val AVATAR_OFFSET = 15.dp
private val EXTRA_BUBBLE_OFFSET = 6.dp

@Composable
fun MemberAvatarStack(
    members: List<ShareUser>,
    isSelected: Boolean
) {
    val displayCount = minOf(members.size, 3)
    val extraCount   = members.size - displayCount

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        Layout(
            content = {
                if (extraCount > 0) {
                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$extraCount",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                // We draw in reverse order so the first one ends up on top
                repeat(displayCount) { index ->
                    val memberIndex = displayCount - 1 - index
                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), CircleShape)
                    ) {
                        members.getOrNull(memberIndex)?.let { user ->
                            UserAvatar(
                                username = user.username,
                                size = AVATAR_SIZE.value.toInt(),
                                profilePictureURL = user.profilePictureURL
                            )
                        }
                    }
                }
            }
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val avatarSize = placeables.firstOrNull()?.width ?: 0
            val offset = (AVATAR_SIZE - AVATAR_OFFSET).roundToPx()
            val extraBubbleSpace = EXTRA_BUBBLE_OFFSET.roundToPx()
            val itemCount = placeables.size
            val totalWidth = if (itemCount > 0) {
                val base = avatarSize + (offset * (itemCount - 1))
                if (itemCount > 3) base + extraBubbleSpace else base
            } else 0
            val height = placeables.firstOrNull()?.height ?: 0

            layout(totalWidth, height) {
                placeables.forEachIndexed { index, placeable ->
                    var x = (itemCount - 1 - index) * offset
                    if (itemCount > 3 && index == 0) x += extraBubbleSpace
                    placeable.placeWithLayer(x = x, y = 0, zIndex = index.toFloat())
                }
            }
        }
    }
}
