package com.oracle.visualize.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.presentation.navigation.NavItem
import com.oracle.visualize.presentation.navigation.NavRoutes

/**
 * A stateless UI component for the Bottom Navigation Bar.
 * * @param navItems The list of navigation items to display.
 * @param currentDestination The current active route object/class for selection highlighting.
 * @param onItemSelected Callback triggered when a navigation item is tapped.
 */
@Composable
fun BottomNavBar(
    navItems: List<NavItem>,
    currentDestination: NavRoutes?,
    onItemSelected: (NavRoutes) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        navItems.forEach { item ->
            val isSelected = currentDestination?.let { it::class == item.destination::class } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.destination) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge { Text(text = item.badgeCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.label),
                            modifier =
                                if (isSelected)
                                    Modifier.size(29.dp)
                                else
                                    Modifier.size(24.dp)
                        )
                    }
                },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
     }
 }
}
