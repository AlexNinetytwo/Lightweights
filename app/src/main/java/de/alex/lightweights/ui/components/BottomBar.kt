package de.alex.lightweights.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.FitnessCenter


@Composable
fun BottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Outlined.FitnessCenter, null) },
            label = { Text("Track") }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.Outlined.ShowChart, null) },
            label = { Text("Progress") }
        )
    }
}
