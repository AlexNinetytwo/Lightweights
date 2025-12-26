package de.alex.lightweights.ui.track

import android.os.Build
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
import calculateStrength
import de.alex.lightweights.ui.components.StrengthChart

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    exerciseName: String,
    onBack: () -> Unit,
    viewModel: ExerciseDetailViewModel = ExerciseDetailViewModel(),
) {
    var enteredWeight by remember { mutableStateOf("") }
    var enteredReps by remember { mutableStateOf("") }
    val isValid = enteredWeight.isNotBlank() && enteredReps.isNotBlank()
    val entries by viewModel.entries.collectAsState()
    val strengthValues = entries
        .filter { it.exerciseId == exerciseId }
        .map { calculateStrength(it) }


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
                value = enteredWeight,
                onValueChange = { enteredWeight = it },
                label = { Text("Gewicht (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = enteredReps,
                onValueChange = { enteredReps = it },
                label = { Text("Wiederholungen") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.addTrainingEntry(
                        exerciseId = exerciseId,
                        weight = enteredWeight.toFloat(),
                        reps = enteredReps.toInt()
                    )
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

