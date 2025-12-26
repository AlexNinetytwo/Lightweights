package de.alex.lightweights

import TrackScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.alex.lightweights.ui.theme.LightweightsTheme
import de.alex.lightweights.ui.track.ExerciseDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LightweightsTheme {
                var selectedExercise by remember { mutableStateOf<String?>(null) }

                if (selectedExercise == null) {
                    TrackScreen(
                        onExerciseClick = { exercise ->
                            selectedExercise = exercise
                        }
                    )
                } else {
                    ExerciseDetailScreen(
                        exerciseName = selectedExercise!!,
                        onBack = { selectedExercise = null }
                    )
                }
            }
        }
    }
}
