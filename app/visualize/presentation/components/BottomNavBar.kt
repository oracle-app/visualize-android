package com.oracle.visualize.presentation.components

<<<<<<< Updated upstream
import androidx.compose.material3.*
=======
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
>>>>>>> Stashed changes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
<<<<<<< Updated upstream
    currentDestination: NavRoutes?,
    onItemSelected: (NavRoutes) -> Unit
=======
    navController: NavController, // NavController replaces selectedIndex + onItemSelected
>>>>>>> Stashed changes
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
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
                        },
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.label)
                        )
                    }
                },
                label = { Text(text = stringResource(item.label)) },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        }
    }
}
