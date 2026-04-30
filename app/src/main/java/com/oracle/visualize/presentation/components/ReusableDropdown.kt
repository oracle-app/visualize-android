package com.oracle.visualize.presentation.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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
    items: List<Pair<String, () -> Unit>>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        items.forEach { (label, onClick) ->
            DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                    onClick()
                    onDismiss()
                }
            )
        }
    }
}