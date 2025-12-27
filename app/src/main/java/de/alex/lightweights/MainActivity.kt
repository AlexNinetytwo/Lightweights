package de.alex.lightweights

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.alex.lightweights.ui.theme.LightweightsTheme
import de.alex.lightweights.ui.track.AddExerciseScreen
import de.alex.lightweights.ui.track.ExerciseDetailScreen
import de.alex.lightweights.ui.track.TrackScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightweightsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Die NavController-Instanz wird hier erstellt und verwaltet
                    val navController = rememberNavController()
                    // Der Navigationsgraph wird hier aufgerufen
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}

// Eine eigene Composable-Funktion für den Navigationsgraphen.
// Das macht den Code sauber und verhindert Context-Probleme.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "track" // Start-Screen ist korrekt
    ) {

        // Route 1: Der TrackScreen
        composable("track") {
            TrackScreen(
                onExerciseClick = { exercise ->
                    // Navigation ZU "detail" wird hier sicher aufgerufen
                    navController.navigate("detail/${exercise.id}/${exercise.name}")
                },
                onAddExerciseClick = {
                    // Navigation ZU "addExercise"
                    navController.navigate("addExercise")
                }
            )
        }

        // Route 2: Der DetailScreen
        composable(
            route = "detail/{exerciseId}/{exerciseName}",
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType },
                navArgument("exerciseName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Die Argumente werden hier sicher aus dem backStackEntry extrahiert
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            val exerciseName = backStackEntry.arguments?.getString("exerciseName")

            // Sicherheitsprüfung, falls Argumente fehlen
            if (exerciseId != null && exerciseName != null) {
                ExerciseDetailScreen(
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    onBack = {
                        // Navigation ZURÜCK vom DetailScreen
                        navController.popBackStack()
                    }
                )
            }
        }

        // Route 3: Der AddExerciseScreen
        composable("addExercise") {
            AddExerciseScreen(
                onDone = {
                    // Navigation ZURÜCK vom AddExerciseScreen
                    navController.popBackStack()
                }
            )
        }
    }
}
