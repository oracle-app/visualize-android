package com.oracle.visualize.presentation.screens.feedScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.presentation.components.UserAvatar

private val AVATAR_SIZE    = 33.dp
private val AVATAR_OFFSET  = 16.dp

/**
 * Composable that displays a stack of user avatars.
 * Used in the feed to show who a visualization is shared with.
 *
 * @param members List of [User] whose avatars will be displayed.
 */
@Composable
fun MemberAvatarStackFeed(
    members: List<User>,
) {
    // If there are exactly 4 members, show all 4 avatars.
    // If there are more than 4, show 3 avatars and an overflow bubble (e.g., +2 for 5 members).
    val displayCount = if (members.size == 4) 4 else minOf(members.size, 3)
    val extraCount = members.size - displayCount
    val showExtraBubble = extraCount > 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth().padding(horizontal = 14.dp)
    ) {
        Layout(
            content = {
                if (showExtraBubble) {
                    Box(
                        modifier = Modifier
                            .requiredSize(AVATAR_SIZE)
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$extraCount",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            color = MaterialTheme.colorScheme.onSurface
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
            val extraBubbleSpace = if (showExtraBubble) 22 else 0
            val itemCount = placeables.size
            val totalWidth = if (itemCount > 0) avatarSize + (offset * (itemCount - 1)) + extraBubbleSpace else 0
            val height = placeables.firstOrNull()?.height ?: 0

            layout(totalWidth, height) {
                placeables.forEachIndexed { index, placeable ->
                    // index 0 = extra bubble or last member, last index = first member (on top)
                    var x = (itemCount - 1 - index) * offset
                    if (showExtraBubble && index == 0) x += extraBubbleSpace
                    placeable.placeWithLayer(x, 0, zIndex = index.toFloat())
                }
            }
        }
    }
}
