package com.oracle.visualize.ui.screens.chartselection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oracle.visualize.ui.components.BottomNavBar

/**
 * Bottom action buttons and navigation for chart selection.
 */
@Composable
fun ChartSelectionBottomActions() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* Post to personal feed */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A23C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Post to personal feed",
                    color = Color.White
                )
            }
            Button(
                onClick = { /* Share and post */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A23C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Share and post",
                    color = Color.White
                )
            }
        }
        BottomNavBar()
    }
}
