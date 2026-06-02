package com.oracle.visualize.presentation.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.oracle.visualize.domain.models.DonutChart
import io.github.koalaplot.core.util.toString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Renders a tooltips based on the provided data for [PieChart] and [DonutChart].
 *
 * @param modifier The composable Modifier variable so a parent component can
 * modify its appearance.
 * @param percentageValue The percentage value to be displayed.
 * @param categoryName The category name to be displayed.
 * @param categoryValue The category value to be displayed.
 * @param enableTooltip Enables or disables the property of tooltips to be shown.
 * @param coroutineScope Coroutine that lets tap detection show a tooltip.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieDonutToolTipLabelBox(
    modifier: Modifier = Modifier, percentageValue: Float, categoryName: String,
    categoryValue: Float, enableTooltip: Boolean, coroutineScope: CoroutineScope
) {
    if (!enableTooltip) {
        Box(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(12.dp))
        ) {
            Text(
                modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                text = "${percentageValue.toString(2)} %", color = Color.DarkGray
            )
        }
    } else {
        val tooltipDisplayState = rememberTooltipState(
            initialIsVisible = false, isPersistent = true
        )

        TooltipBox(
            tooltip = { PlainTooltip { Text(text = "$categoryName: $categoryValue") } },
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                positioning = TooltipAnchorPosition.Above
            ),
            state = tooltipDisplayState,
        ) {
            Box(
                modifier = modifier
                    .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(12.dp))
                    .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { coroutineScope.launch { tooltipDisplayState.show() } })
                    }
            ) {
                Text(
                    modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                    text = "${percentageValue.toString(2)} %", color = Color.DarkGray
                )
            }
        }
    }
}
