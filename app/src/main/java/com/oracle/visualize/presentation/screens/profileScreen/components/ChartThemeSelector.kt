package com.oracle.visualize.presentation.screens.profileScreen.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.ChartPalette
import androidx.compose.ui.res.stringResource

private var toast: Toast? = null

@Composable
fun ChartThemePicker(
    selectedPalette: String,
    onPaletteChange: (String) -> Unit
) {
    val context = LocalContext.current

    // Theme names
    val changedThemeText = stringResource(R.string.chart_changed_to_theme)
    val lagoon = stringResource(R.string.chart_theme_lagoon)
    val sunset = stringResource(R.string.chart_theme_sunset)
    val harvest = stringResource(R.string.chart_theme_harvest)
    val petal = stringResource(R.string.chart_theme_petal)

    SettingsCard(title = stringResource(R.string.chart_theme_title), titleDrawableImage = R.drawable.baseline_color_lens_24) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                ThemeItem(
                    palette = ChartPalette.THEME1,
                    isSelected = selectedPalette == ChartPalette.THEME1.name,
                    onClick = {
                        toast?.cancel()
                        onPaletteChange(ChartPalette.THEME1.name)
                        toast = Toast.makeText(context, "$changedThemeText $lagoon", Toast.LENGTH_SHORT)
                        toast?.show()
                    },
                    modifier = Modifier.weight(1f)
                )
                ThemeItem(
                    palette = ChartPalette.THEME2,
                    isSelected = selectedPalette == ChartPalette.THEME2.name,
                    onClick = {
                        toast?.cancel()
                        onPaletteChange(ChartPalette.THEME2.name)
                        toast = Toast.makeText(context, "$changedThemeText $sunset", Toast.LENGTH_SHORT)
                        toast?.show()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                ThemeItem(
                    palette = ChartPalette.THEME3,
                    isSelected = selectedPalette == ChartPalette.THEME3.name,
                    onClick = {
                        toast?.cancel()
                        onPaletteChange(ChartPalette.THEME3.name)
                        toast = Toast.makeText(context, "$changedThemeText $harvest", Toast.LENGTH_SHORT)
                        toast?.show()
                    },
                    modifier = Modifier.weight(1f)
                )
                ThemeItem(
                    palette = ChartPalette.THEME4,
                    isSelected = selectedPalette == ChartPalette.THEME4.name,
                    onClick = {
                        toast?.cancel()
                        onPaletteChange(ChartPalette.THEME4.name)
                        toast = Toast.makeText(context, "$changedThemeText $petal", Toast.LENGTH_SHORT)
                        toast?.show()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
