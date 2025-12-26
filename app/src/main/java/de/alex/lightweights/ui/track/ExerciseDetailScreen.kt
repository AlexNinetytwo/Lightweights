package de.alex.lightweights.ui.track

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.alex.lightweights.domain.calculateStrength
import de.alex.lightweights.domain.TrainingEntry
import de.alex.lightweights.ui.components.StrengthChart
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseName: String,
    onBack: () -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    val isValid = weight.isNotBlank() && reps.isNotBlank()
    val entries = listOf(
        TrainingEntry("1", LocalDate.now().minusDays(23), 50f, 8),
        TrainingEntry("1", LocalDate.now().minusDays(21), 52f, 8),
        TrainingEntry("1", LocalDate.now().minusDays(20), 52f, 10),
        TrainingEntry("1", LocalDate.now().minusDays(18), 54.5f, 9),
        TrainingEntry("1", LocalDate.now().minusDays(15), 55f, 8),
        TrainingEntry("1", LocalDate.now().minusDays(13), 55f, 9),
        TrainingEntry("1", LocalDate.now().minusDays(12), 55f, 10),
        TrainingEntry("1", LocalDate.now().minusDays(9), 60f, 9),
        TrainingEntry("1", LocalDate.now().minusDays(7), 63f, 10),
        TrainingEntry("1", LocalDate.now().minusDays(5), 62.5f, 8),
        TrainingEntry("1", LocalDate.now().minusDays(3), 65f, 6),
    )

    val strengthValues = entries.map {
        calculateStrength(it)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exerciseName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Trainingseintrag",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Gewicht (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Wiederholungen") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    // TODO: später speichern
                    Log.d("Detail", "Saved $weight kg x $reps")
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Speichern")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Stärke-Verlauf",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            StrengthChart(
                values = strengthValues,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}

@Composable
fun TrainingEntryItem(
    weight: String,
    reps: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$weight kg", style = MaterialTheme.typography.titleMedium)
            Text("$reps Wdh", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

