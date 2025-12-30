package de.alex.lightweights.ui.track

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteExerciseDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Übung wirklich löschen?") },

        confirmButton = {
            TextButton(
                onClick = {
                    onAccept()
                    onDismiss()
                }
            ) {
                Text("Ja")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}