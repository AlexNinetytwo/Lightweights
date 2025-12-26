package de.alex.lightweights

import AddExerciseScreen
import TrackScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.alex.lightweights.ui.theme.LightweightsTheme
import de.alex.lightweights.ui.track.ExerciseDetailScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightweightsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "track"
                ) {

                    composable("track") {
                        TrackScreen(
                            onExerciseClick = { exerciseName ->
                                navController.navigate("detail/$exerciseName/$exerciseName")
                            },
                            onAddExerciseClick = {
                                navController.navigate("addExercise")
                            }
                        )
                    }

                    composable(
                        route = "detail/{exerciseId}/{exerciseName}",
                        arguments = listOf(
                            navArgument("exerciseId") { type = NavType.StringType },
                            navArgument("exerciseName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->

                        ExerciseDetailScreen(
                            exerciseId = backStackEntry.arguments!!
                                .getString("exerciseId")!!,
                            exerciseName = backStackEntry.arguments!!
                                .getString("exerciseName")!!,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("addExercise") {
                        AddExerciseScreen(
                            onDone = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
