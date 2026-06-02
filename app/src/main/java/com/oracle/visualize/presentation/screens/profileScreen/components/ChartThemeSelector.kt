package com.oracle.visualize.presentation.screens.profileScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.ChartPalette
import androidx.compose.ui.res.stringResource

@Composable
fun ChartThemePicker(
    selectedPalette: String,
    onPaletteChange: (String) -> Unit
) {
    SettingsCard(title = stringResource(R.string.chart_theme_title)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                ThemeItem(
                    palette = ChartPalette.THEME1,
                    isSelected = selectedPalette == ChartPalette.THEME1.name,
                    onClick = { onPaletteChange(ChartPalette.THEME1.name) },
                    modifier = Modifier.weight(1f)
                )
                ThemeItem(
                    palette = ChartPalette.THEME2,
                    isSelected = selectedPalette == ChartPalette.THEME2.name,
                    onClick = { onPaletteChange(ChartPalette.THEME2.name) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                ThemeItem(
                    palette = ChartPalette.THEME3,
                    isSelected = selectedPalette == ChartPalette.THEME3.name,
                    onClick = { onPaletteChange(ChartPalette.THEME3.name) },
                    modifier = Modifier.weight(1f)
                )
                ThemeItem(
                    palette = ChartPalette.THEME4,
                    isSelected = selectedPalette == ChartPalette.THEME4.name,
                    onClick = { onPaletteChange(ChartPalette.THEME4.name) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
