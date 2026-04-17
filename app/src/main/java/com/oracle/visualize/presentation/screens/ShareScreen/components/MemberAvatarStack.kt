package com.oracle.visualize.presentation.screens.ShareScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.ui.theme.TextDark

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
    val extraCount = members.size - displayCount

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        Box(
            modifier = Modifier
                .width((displayCount * 14 + 28).dp)
                .height(28.dp)
        ) {
            repeat(displayCount) { index ->
                Box(
                    modifier = Modifier
                        .offset(x = (index * 14).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AVATAR_COLORS[index % AVATAR_COLORS.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = members.getOrNull(index)?.avatarInitials ?: "",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        if (extraCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.25f)
                        else Color(0xFFCDD5D8)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$extraCount",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextDark
                )
            }
        }
    }
}