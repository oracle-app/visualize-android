package com.oracle.visualize.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Example usage:
// AppDropdownMenu(
//     expanded = showMenu,
//     onDismiss = { showMenu = false },
//     items = listOf(
//         "Option 1" to { doSomething() },
//         "Option 2" to { doSomethingElse() }
//     )
// )

// IMPORTANT: Do not forget to use
// stringResource(R.string.string_name_here)
// instead of raw strings.

// Thank you!

@Composable
fun AppDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    offset: DpOffset = DpOffset(x = 0.dp, y = 0.dp),
    items: List<Pair<String, () -> Unit>>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = offset,
        modifier = Modifier
            .width(200.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items.forEach { (label, onClick) ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onClick()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            )
        }
    }
}
