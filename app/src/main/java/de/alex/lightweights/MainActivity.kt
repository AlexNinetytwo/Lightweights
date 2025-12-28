package de.alex.lightweights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.alex.lightweights.ui.components.BottomBar
import de.alex.lightweights.ui.progress.ProgressScreen
import de.alex.lightweights.ui.theme.LightweightsTheme
import de.alex.lightweights.ui.track.AddExerciseScreen
import de.alex.lightweights.ui.track.ExerciseDetailScreen
import de.alex.lightweights.ui.track.TrackScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightweightsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoute.Track.route,
        NavRoute.Progress.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Track.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(NavRoute.Track.route) {
                TrackScreen(
                    onExerciseClick = { exercise ->
                        navController.navigate(
                            NavRoute.ExerciseDetail.createRoute(
                                exercise.id,
                                exercise.name
                            )
                        )
                    },
                    onAddExerciseClick = {
                        navController.navigate(NavRoute.AddExercise.route)
                    }
                )
            }

            composable(NavRoute.Progress.route) {
                ProgressScreen() // erstmal leer
            }

            composable(
                route = NavRoute.ExerciseDetail.route,
                arguments = listOf(
                    navArgument("exerciseId") { type = NavType.StringType },
                    navArgument("exerciseName") { type = NavType.StringType }
                )
            ) { entry ->
                ExerciseDetailScreen(
                    exerciseId = entry.arguments!!.getString("exerciseId")!!,
                    exerciseName = entry.arguments!!.getString("exerciseName")!!,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoute.AddExercise.route) {
                AddExerciseScreen(
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
