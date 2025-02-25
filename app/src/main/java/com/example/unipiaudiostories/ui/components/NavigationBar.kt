package com.example.unipiaudiostories.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.unipiaudiostories.R

data class NavItem(val route: String, var label: String, val icon: ImageVector)

@Composable
fun AdaptiveNavigationBar(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
) {
    val navItems = listOf(
        NavItem("home", stringResource(R.string.home), Icons.Default.Home),
        NavItem("stats", stringResource(R.string.stats), Icons.Filled.EmojiEvents),
        NavItem("profile", stringResource(R.string.profile), Icons.Default.Person)
    )
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
        NavigationRail {
            navItems.forEach { item ->
                NavigationRailItem(
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = { navController.navigate(item.route) },
                    label = { Text(item.label) },
                    icon = { Icon(item.icon, contentDescription = null) }
                )
            }
        }
    } else {
        NavigationBar {
            navItems.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = { navController.navigate(item.route) },
                    label = { Text(item.label) },
                    icon = { Icon(item.icon, contentDescription = null) }
                )
            }
        }
    }
}

@Composable
fun AdaptiveScaffold(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
        Row(modifier = Modifier.fillMaxSize()) {
            AdaptiveNavigationBar(navController = navController, windowSizeClass = windowSizeClass)
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                content()
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                AdaptiveNavigationBar(navController = navController, windowSizeClass = windowSizeClass)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}