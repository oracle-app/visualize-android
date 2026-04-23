package com.oracle.visualize.presentation.screens.shareScreen.components

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

private val AVATAR_SIZE    = 29.dp
private val AVATAR_OFFSET  = 15.dp

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
                        .border(BorderStroke(1.dp, Color.White), CircleShape)
                ) {
                    members.getOrNull(index)?.let { user ->
                        UserAvatar(
                            user = user,
                            size = AVATAR_SIZE.value.toInt()
                        )
                    }
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
                    .border(BorderStroke(1.dp, Color.White), CircleShape),
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