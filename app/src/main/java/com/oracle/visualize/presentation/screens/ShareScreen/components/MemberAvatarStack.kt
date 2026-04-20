package com.oracle.visualize.presentation.screens.ShareScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareUser

private val AVATAR_SIZE = 29.dp
private val AVATAR_OFFSET = 15.dp
private val AVATAR_BORDER = Color(0xFFE6EDEC)

private val AVATAR_COLORS = listOf(
    Color(0xFFE8A87C),
    Color(0xFF7EC8C8),
    Color(0xFFB8D4E8),
    Color(0xFFE8C87C),
    Color(0xFF8CB87C)
)

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
        // Stacked avatars box — width = offset*(n-1) + avatarSize
        val stackWidth = if (displayCount > 0)
            AVATAR_OFFSET * (displayCount - 1) + AVATAR_SIZE
        else 0.dp

        Box(
            modifier = Modifier
                .width(stackWidth)
                .height(AVATAR_SIZE)
        ) {
            repeat(displayCount) { index ->
                Box(
                    modifier = Modifier
                        .offset(x = AVATAR_OFFSET * index)
                        .requiredSize(AVATAR_SIZE)
                        .clip(CircleShape)
                        .background(AVATAR_COLORS[index % AVATAR_COLORS.size])
                        .border(BorderStroke(1.dp, AVATAR_BORDER), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = members.getOrNull(index)?.avatarInitials ?: "",
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        if (extraCount > 0) {
            Spacer(modifier = Modifier.width(1.dp))
            Box(
                modifier = Modifier
                    .requiredSize(AVATAR_SIZE)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.25f) else Color.White
                    )
                    .border(BorderStroke(1.dp, AVATAR_BORDER), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$extraCount",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = if (isSelected) Color.White else Color.DarkGray
                )
            }
        }
    }
}
