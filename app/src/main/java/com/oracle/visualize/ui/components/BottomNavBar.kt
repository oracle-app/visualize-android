package com.oracle.visualize.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Global Bottom Navigation Bar component.
 */
@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = Color(0xFFE0F2F1),
        tonalElevation = 8.dp
    ) {
        val items = listOf<Triple<String, ImageVector, Boolean>>(
            Triple("Create", Icons.Default.Add, true),
            Triple("Teams", Icons.Default.Person, false),
            Triple("Feed", Icons.Default.Home, false),
            Triple("Notifications", Icons.Default.Notifications, false),
            Triple("Profile", Icons.Default.AccountCircle, false)
        )

        items.forEach { (label, icon, selected) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                selected = selected,
                onClick = { /* Handle navigation */ },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF004D40),
                    selectedTextColor = Color(0xFF004D40),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
