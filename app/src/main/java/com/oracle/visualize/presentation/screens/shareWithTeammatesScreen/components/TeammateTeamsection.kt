package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.presentation.screens.shareScreen.components.TeamRow
import com.oracle.visualize.presentation.screens.shareScreen.components.TeamRowPosition

/**
 * Displays a labeled section of teams (either "My teams" or "Teams I'm in")
 * with toggle selection. Reuses [TeamRow] from ShareAndPost for visual consistency.
 *
 * @param title          Section header label.
 * @param teams          List of [ShareTeam] to display.
 * @param selectedTeamIds Set of currently selected team IDs.
 * @param onToggle       Called with the team ID when a row is tapped.
 */
@Composable
fun TeammateTeamSection(
    title: String,
    teams: List<ShareTeam>,
    selectedTeamIds: Set<String>,
    onToggle: (String) -> Unit
) {
    if (teams.isEmpty()) return

    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier   = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                teams.forEachIndexed { index, team ->
                    val position = when {
                        teams.size == 1          -> TeamRowPosition.SINGLE
                        index == 0               -> TeamRowPosition.TOP
                        index == teams.lastIndex -> TeamRowPosition.BOTTOM
                        else                     -> TeamRowPosition.MIDDLE
                    }

                    TeamRow(
                        team       = team,
                        isSelected = team.id in selectedTeamIds,
                        onToggle   = { onToggle(team.id) },
                        position   = position
                    )
                }
            }
        }
    }
}
