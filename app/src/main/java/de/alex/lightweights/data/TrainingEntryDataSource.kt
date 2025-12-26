package de.alex.lightweights.data

import de.alex.lightweights.domain.model.TrainingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrainingEntryDataSource {

    private val _entries = MutableStateFlow<List<TrainingEntry>>(emptyList())
    val entries: StateFlow<List<TrainingEntry>> = _entries

    fun addEntry(entry: TrainingEntry) {
        _entries.value = _entries.value + entry
    }

    fun getEntriesForExercise(exerciseId: String): List<TrainingEntry> {
        return _entries.value.filter { it.exerciseId == exerciseId}
    }
}
