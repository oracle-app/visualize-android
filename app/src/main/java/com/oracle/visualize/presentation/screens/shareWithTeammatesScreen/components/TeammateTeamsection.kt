package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text       = title,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.onBackground,
            modifier   = Modifier.padding(bottom = 8.dp)
        )

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
