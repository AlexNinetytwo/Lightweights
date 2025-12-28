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
import calculateStrength
import de.alex.lightweights.domain.model.TrainingEntry
import de.alex.lightweights.ui.components.StrengthChart
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class TimeFilter { ALL, YEAR, QUARTER, MONTH }

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
    val isValid = enteredWeight.isNotBlank() && enteredReps.isNotBlank()
    val entries by viewModel.entries.collectAsState(emptyList())

    val exerciseEntries = entries
        .filter { it.exerciseId == exerciseId }
        .sortedByDescending { it.date }

    val groupedEntries = exerciseEntries.groupBy { it.date }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(TimeFilter.ALL) }

    val scope = rememberCoroutineScope()

    val filteredChartData by remember(entries, selectedFilter, exerciseId) {
        derivedStateOf {
            // 1. Filtere nach Übung und sortiere (wichtig für die Chart-Reihenfolge)
            val dailyAggregatedData = entries
                .filter { it.exerciseId == exerciseId }
                .sortedBy { it.date }
                // 2. Gruppiere alle Einträge nach ihrem Datum
                .groupBy { it.date }
                // 3. Transformiere die Werte jeder Gruppe:
                //    Summiere die Stärke aller Einträge für diesen Tag auf.
                .mapValues { (_, dayEntries) ->
                    dayEntries.sumOf { calculateStrength(it).toDouble() }.toFloat()
                }
                // 4. Konvertiere die Map zurück in eine Liste von Paaren.
                //    Das Ergebnis ist z.B.: [(2023-09-22, 150.5), (2023-09-24, 160.0)]
                .toList()

            // Der Filter-Code ab hier bleibt gleich und funktioniert weiterhin perfekt.
            if (dailyAggregatedData.isEmpty()) {
                return@derivedStateOf emptyList()
            }

            val today = LocalDate.now()
            val limitDate = when (selectedFilter) {
                TimeFilter.YEAR -> today.minusYears(1)
                TimeFilter.QUARTER -> today.minusMonths(3)
                TimeFilter.MONTH -> today.minusMonths(1)
                TimeFilter.ALL -> null // Kein Zeitlimit
            }

            if (limitDate != null) {
                dailyAggregatedData.filter { (date, _) -> date.isAfter(limitDate) }
            } else {
                dailyAggregatedData
            }
        }
    }

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
                    scope.launch {
                        viewModel.addTrainingEntry(
                            exerciseId = exerciseId,
                            weight = enteredWeight.toFloat(),
                            reps = enteredReps.toInt(),
                            date = selectedDate
                        )
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
                    // 3. Füge die Filter-Buttons hinzu
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
