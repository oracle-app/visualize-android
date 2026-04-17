package com.oracle.visualize.presentation.screens.ShareScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.ui.theme.TextDark
import com.oracle.visualize.ui.theme.TextGray

private val TealSelected = Color(0xFF3D8C84)
private val TeamUnselectedBg = Color(0xFFE5ECEB)

@Composable
fun TeamRow(
    team: ShareTeam,
    onToggle: () -> Unit
) {
    val bgColor = if (team.isSelected) TealSelected else TeamUnselectedBg
    val textColor = if (team.isSelected) Color.White else TextDark
    val subColor = if (team.isSelected) Color.White.copy(alpha = 0.85f) else TextGray

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (team.isSelected) 0.dp else 2.dp, RoundedCornerShape(14.dp))
            .background(bgColor, RoundedCornerShape(14.dp))
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                text = "${team.memberCount} members",
                fontSize = 12.sp,
                color = subColor
            )
        }
        MemberAvatarStack(
            members = team.members,
            isSelected = team.isSelected
        )
    }
}