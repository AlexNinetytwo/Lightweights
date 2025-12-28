package de.alex.lightweights.ui.track

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import de.alex.lightweights.domain.model.Exercise
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EditExerciseDialog(
    exercise: Exercise,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(exercise.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Übung umbenennen") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Name der Übun") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(name)
                    onDismiss() // 👈 SEHR wichtig
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
