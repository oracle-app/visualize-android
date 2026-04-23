package com.oracle.visualize.presentation.screens.shareScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.User

private val AVATAR_SIZE    = 33.dp
private val AVATAR_OFFSET  = 16.dp

@Composable
fun MemberAvatarStackFeed(
    members: List<User>,
) {
    val displayCount = minOf(members.size, 3)
    val extraCount = members.size - displayCount

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth().padding(horizontal = 14.dp)
    ) {
        Layout(
            content = {
                if (extraCount > 0) {
                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, Color.White), CircleShape)
                            .background(Color.White)
                            .padding(start = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$extraCount",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            color = Color.DarkGray
                        )
                    }
                }
                // Dibujamos en orden inverso para que el primero quede encima
                repeat(displayCount) { index ->
                    val memberIndex = displayCount - 1 - index
                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, Color.White), CircleShape)
                    ) {
                        members.getOrNull(memberIndex)?.let { user ->
                            UserAvatarCard(user = user, size = AVATAR_SIZE.value.toInt())
                        }
                    }
                }
            }
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val avatarSize = placeables.firstOrNull()?.width ?: 0
            val offset = (AVATAR_SIZE - AVATAR_OFFSET).roundToPx()
            val totalWidth = if (displayCount > 0)
                avatarSize + (offset * (displayCount - 1))
            else 0
            val height = placeables.firstOrNull()?.height ?: 0

            layout(totalWidth, height) {
                placeables.forEachIndexed { index, placeable ->
                    // index 0 = último member (atrás), index displayCount-1 = primero (adelante)
                    val x = (displayCount - 1 - index) * offset
                    placeable.placeWithLayer(x, 0, zIndex = index.toFloat())
                }
            }
        }


    }
}