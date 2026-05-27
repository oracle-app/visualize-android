package com.oracle.visualize.presentation.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

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
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        offset = offset
    ) {
        items.forEach { (label, onClick) ->
            DropdownMenuItem(
                text = { Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                onClick = {
                    onClick()
                    onDismiss()
                },
                colors = MenuItemColors(
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                )
            )
        }
    }
}
