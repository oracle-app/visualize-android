package com.oracle.visualize.presentation.screens.shareScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareUser

@Composable
fun SuggestedUserRow(
    user: ShareUser,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        UserAvatar(user, size = 44)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.username,
                fontSize = 16.sp,
                color = Color(0xFF1D1B20),
                fontWeight = FontWeight.Medium,
                lineHeight = 1.4.em
            )
            Text(
                text = user.email,
                fontSize = 14.sp,
                color = Color(0xFF597271),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 1.4.em
            )
        }
    }
}