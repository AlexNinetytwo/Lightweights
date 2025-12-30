package de.alex.lightweights.ui.track

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.alex.lightweights.domain.model.TrainingEntry
import de.alex.lightweights.domain.rememberFilteredChartData
import de.alex.lightweights.domain.TimeFilter
import de.alex.lightweights.ui.components.StrengthChart
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    exerciseName: String,
    onBack: () -> Unit,
) {
    val viewModel: ExerciseDetailViewModel = viewModel()

    var enteredWeight by remember { mutableStateOf("") }
    var enteredReps by remember { mutableStateOf("") }
    val isValid by remember(enteredWeight, enteredReps) {
        derivedStateOf {
            val weightAsDouble = enteredWeight.replace(',', '.').toDoubleOrNull()
            val repsAsInt = enteredReps.toIntOrNull()
            weightAsDouble != null && repsAsInt != null
        }
    }
    val entries by viewModel.entries.collectAsState(emptyList())

    val exerciseEntries = entries
        .filter { it.exerciseId == exerciseId }
        .sortedByDescending { it.date }

    val groupedEntries = exerciseEntries.groupBy { it.date }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(TimeFilter.ALL) }

    val scope = rememberCoroutineScope()

    var maxReps by remember { mutableDoubleStateOf(12.0) }
    var cutoff by remember { mutableDoubleStateOf(0.18) }

    val filteredChartData by rememberFilteredChartData(entries, exerciseId, selectedFilter, maxReps, cutoff)

    var editingEntry by remember { mutableStateOf<TrainingEntry?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exerciseName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                onValueChange = {
                    enteredWeight = it.filter { char -> char.isDigit() || char == '.' || char == ',' }
                },
                label = { Text("Gewicht (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = enteredReps,
                onValueChange = {
                    enteredReps = it.filter { char -> char.isDigit() }
                },
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
                    scope.launch {
                        val weightValue = enteredWeight.replace(',', '.').toFloatOrNull()
                        val repsValue = enteredReps.toIntOrNull()
                        if (weightValue != null && repsValue != null) {
                            viewModel.addTrainingEntry(
                                exerciseId = exerciseId,
                                weight = weightValue,
                                reps = repsValue,
                                date = selectedDate
                            )
                            enteredReps = ""
                        }
                    }
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
//                    Text("Max. relevante Wiederholungen: $maxReps")
//                    Slider(
//                        value = maxReps.toFloat(),
//                        onValueChange = { maxReps = it.toDouble() },
//                        valueRange = 5f..20f,
//                        steps = 14
//                    )
//
//                    Text("Sättigungs-Faktor: ${"%.2f".format(cutoff)}")
//                    Slider(
//                        value = cutoff.toFloat(),
//                        onValueChange = { cutoff = it.toDouble() },
//                        valueRange = 0.05f..0.5f
//                    )

                    FilterButtons(
                        selected = selectedFilter,
                        onFilterSelected = { newFilter -> selectedFilter = newFilter }
                    )

                    StrengthChart(
                        chartData = filteredChartData,
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
                            entry = entry,
                            onEdit = { editingEntry = entry },
                            onDelete = {
                                viewModel.deleteTrainingEntry(entry)
                            }
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

    if (editingEntry != null) {
        EditTrainingEntryDialog(
            entry = editingEntry!!,
            onSave = { weight, reps ->
                viewModel.updateTrainingEntry(
                    editingEntry!!,
                    weight,
                    reps
                )
                editingEntry = null
            },
            onDismiss = { editingEntry = null }
        )
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
    entry: TrainingEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { expanded = true }
            ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${entry.weight} kg")
                Text("${entry.reps} Wdh")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Bearbeiten") },
                    onClick = {
                        expanded = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Löschen") },
                    onClick = {
                        expanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
fun FilterButtons(
    selected: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onFilterSelected(TimeFilter.ALL) }, enabled = selected != TimeFilter.ALL) { Text("Gesamt") }
        Button(onClick = { onFilterSelected(TimeFilter.YEAR) }, enabled = selected != TimeFilter.YEAR) { Text("Jahr") }
        Button(onClick = { onFilterSelected(TimeFilter.QUARTER) }, enabled = selected != TimeFilter.QUARTER) { Text("3M") }
        Button(onClick = { onFilterSelected(TimeFilter.MONTH) }, enabled = selected != TimeFilter.MONTH) { Text("1M") }
    }
}
