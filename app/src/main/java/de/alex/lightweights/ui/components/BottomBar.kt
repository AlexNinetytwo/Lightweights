package de.alex.lightweights.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.alex.lightweights.NavRoute
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ShowChart


@Composable
fun BottomBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == NavRoute.Track.route,
            onClick = {
                navController.navigate(NavRoute.Track.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.FitnessCenter, null) },
            label = { Text("Track") }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoute.Progress.route,
            onClick = {
                navController.navigate(NavRoute.Progress.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, null) },
            label = { Text("Progress") }
        )
    }
}
