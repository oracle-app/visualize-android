package com.oracle.visualize.presentation.screens.ShareScreen.components

import androidx.compose.animation.animateColorAsState

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.ui.theme.TealPrimary
import com.oracle.visualize.ui.theme.TextDark

private val TeamUnselectedBg = Color(0xFFE5ECEB)
private val SubtextSelected  = Color(0xFFCDE9EA)
private val SubtextUnselected = Color(0xFF597271)

val ShapeTop    = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
val ShapeMiddle = RoundedCornerShape(5.dp)
val ShapeBottom = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 15.dp, bottomEnd = 15.dp)
val ShapeSingle = RoundedCornerShape(15.dp)

enum class TeamRowPosition { SINGLE, TOP, MIDDLE, BOTTOM }

@Composable
fun TeamRow(
    team: ShareTeam,
    onToggle: () -> Unit,
    position: TeamRowPosition = TeamRowPosition.SINGLE
) {
    val shape = when (position) {
        TeamRowPosition.TOP    -> ShapeTop
        TeamRowPosition.MIDDLE -> ShapeMiddle
        TeamRowPosition.BOTTOM -> ShapeBottom
        TeamRowPosition.SINGLE -> ShapeSingle
    }

    val targetBg = if (team.isSelected) TealPrimary else TeamUnselectedBg
    val animatedBg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(200),
        label = "teamBg"
    )
    val textColor = if (team.isSelected) Color.White else TextDark
    val subColor  = if (team.isSelected) SubtextSelected else SubtextUnselected

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(animatedBg)
            .clickable {
                onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = team.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = textColor,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${team.memberCount} members",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = subColor,
                lineHeight = 20.sp
            )
        }
        MemberAvatarStack(members = team.members, isSelected = team.isSelected)
    }
}
