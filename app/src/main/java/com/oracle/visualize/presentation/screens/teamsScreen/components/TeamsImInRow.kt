package com.oracle.visualize.presentation.screens.teamsScreen.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.components.UserAvatar
import com.oracle.visualize.presentation.screens.shareScreen.components.MemberAvatarStack


@Composable
fun TeamsImInRow(
    team: ShareTeam,
    isExpanded: Boolean,
    position: TeamPosition,
    onToggle: () -> Unit
) {
    val teamNameColor = MaterialTheme.colorScheme.onSurface

    val arrowTint = MaterialTheme.colorScheme.onSurface

    val amountOfMembersTextColor = MaterialTheme.colorScheme.onSecondaryContainer

    Column(
        modifier = Modifier.fillMaxWidth().clip(teamShape(position))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
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
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector        = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint               = arrowTint
            )
        }

        AnimatedVisibility(visible = isExpanded, enter = expandVertically(), exit = shrinkVertically()) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                team.members.forEach { member ->
                    MemberListItem(user = member, isOwner = member.id == team.ownerID)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MemberListItem(user: ShareUser, isOwner: Boolean) {
    val memberNameColor = MaterialTheme.colorScheme.onSurface

    val ownerOrEmailColor = MaterialTheme.colorScheme.primary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.fillMaxWidth()
    ) {
        UserAvatar(username = user.username, profilePictureURL = user.profilePictureURL, size = 40)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = user.username,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = memberNameColor
            )
            Text(
                text     = user.email,
                fontSize = 12.sp,
                color    = ownerOrEmailColor
            )
        }
        if (isOwner) {
            Text(
                text     = stringResource(R.string.teams_owner_label),
                fontSize = 12.sp,
                color    = ownerOrEmailColor
            )
        }
    }
}
