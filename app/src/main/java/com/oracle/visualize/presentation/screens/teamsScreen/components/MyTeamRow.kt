package com.oracle.visualize.presentation.screens.teamsScreen.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.presentation.screens.shareScreen.components.MemberAvatarStack

// Each row is fully rounded — matches Figma where every card is independent
private val ROW_SHAPE = RoundedCornerShape(16.dp)

// Keep TeamPosition and teamShape for TeamsImInRow which still uses grouped style
enum class TeamPosition { SINGLE, TOP, MIDDLE, BOTTOM }

fun teamShape(position: TeamPosition) = when (position) {
    TeamPosition.TOP    -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    TeamPosition.MIDDLE -> RoundedCornerShape(4.dp)
    TeamPosition.BOTTOM -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    TeamPosition.SINGLE -> RoundedCornerShape(16.dp)
}

@Composable
fun MyTeamRow(
    team: ShareTeam,
    isSwiped: Boolean,
    position: TeamPosition,
    onSwipe: () -> Unit,
    onDismissSwipe: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val offset by animateDpAsState(targetValue = if (isSwiped) (-140).dp else 0.dp, label = "swipeOffset")

    val teamNameColor = MaterialTheme.colorScheme.onSurface

    val amountOfMembersTextColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(shape = teamShape(position))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Swipe action buttons (revealed on swipe)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(140.dp)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onEdit(); onDismissSwipe() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.teams_edit_description),
                    tint               = MaterialTheme.colorScheme.onPrimary,
                    modifier           = Modifier.size(28.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.teams_delete_description),
                    tint               = MaterialTheme.colorScheme.onPrimary,
                    modifier           = Modifier.size(28.dp)
                )
            }
        }

        // Main row content
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier
                .offset(x = offset)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -15f) onSwipe()
                        if (dragAmount > 15f) onDismissSwipe()
                    }
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = team.name,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color      = teamNameColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = stringResource(R.string.teams_member_count, team.memberCount),
                    fontSize = 14.sp,
                    color    = amountOfMembersTextColor
                )
            }
            MemberAvatarStack(members = team.members, isSelected = false)
        }
    }
}
