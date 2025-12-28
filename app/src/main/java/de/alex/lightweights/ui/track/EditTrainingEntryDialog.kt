package de.alex.lightweights.ui.track

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.alex.lightweights.domain.model.TrainingEntry

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditTrainingEntryDialog(
    entry: TrainingEntry,
    onSave: (Float, Int) -> Unit,
    onDismiss: () -> Unit
) {

    var currentWeight by remember { mutableStateOf(entry.weight.toString()) }
    var currentReps by remember { mutableStateOf(entry.reps.toString()) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eintrag bearbeiten") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Abbrechen"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = currentWeight,
                onValueChange = { currentWeight = it },
                label = { Text("Gewicht (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = currentReps,
                onValueChange = { currentReps = it },
                label = { Text("Wiederholungen") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val weight = currentWeight.toFloatOrNull()
                    val reps = currentReps.toIntOrNull()

                    if (weight != null && reps != null) {
                        onSave(weight, reps)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Speichern")
            }
        }
    }
}