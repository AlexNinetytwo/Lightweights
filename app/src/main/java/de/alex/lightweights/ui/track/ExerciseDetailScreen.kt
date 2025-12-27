package de.alex.lightweights.ui.track

import ExerciseItem
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import calculateStrength
import de.alex.lightweights.ui.components.StrengthChart
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    exerciseName: String,
    onBack: () -> Unit,
    viewModel: ExerciseDetailViewModel = viewModel()
) {
    var enteredWeight by remember { mutableStateOf("") }
    var enteredReps by remember { mutableStateOf("") }
    val isValid = enteredWeight.isNotBlank() && enteredReps.isNotBlank()
    val entries by viewModel.entries.collectAsState()
    val chartData = entries
        .filter { it.exerciseId == exerciseId }
        .sortedBy { it.date }
        .map { it.date to calculateStrength(it) }

    val exerciseEntries = entries
        .filter { it.exerciseId == exerciseId }
        .sortedByDescending { it.date }

    val groupedEntries = exerciseEntries.groupBy { it.date }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }


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

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedDate == LocalDate.now()) {
                        "Datum: Heute"
                    } else {
                        "Datum: ${selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
                    }
                )
            }

            Button(
                onClick = {
                    viewModel.addTrainingEntry(
                        exerciseId = exerciseId,
                        weight = enteredWeight.toFloat(),
                        reps = enteredReps.toInt(),
                        date = selectedDate
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    StrengthChart(
                        chartData = chartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                groupedEntries.forEach { (date, dayEntries) ->

                    item {
                        TrainingDayHeader(date = date)
                    }

                    items(dayEntries) { entry ->
                        TrainingEntryItem(
                            weight = entry.weight,
                            reps = entry.reps
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant
                            .ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun TrainingDayHeader(date: LocalDate) {
    val today = LocalDate.now()

    val title = when (date) {
        today -> "Heute"
        else -> date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TrainingEntryItem(
    weight: Float,
    reps: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$weight kg",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$reps Wdh",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
