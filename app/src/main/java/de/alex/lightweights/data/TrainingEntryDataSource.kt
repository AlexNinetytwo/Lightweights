package de.alex.lightweights.data

import de.alex.lightweights.domain.model.TrainingEntry
import kotlinx.coroutines.flow.Flow

class TrainingEntryDataSource(private val dao: TrainingEntryDao) {

    // Der Flow kommt jetzt direkt vom DAO!
    val entries: Flow<List<TrainingEntry>> = dao.getAllEntries()

    suspend fun addEntry(entry: TrainingEntry) {
        // Die Logik wird an das DAO delegiert
        dao.addEntry(entry)
    }

    // Diese Funktion ist nicht mehr ideal, da sie auf dem letzten Wert des Flows arbeitet.
    // Es ist besser, Abfragen direkt im DAO zu definieren, wenn sie oft gebraucht werden.
    // Für den Moment lassen wir sie als Beispiel, aber sie ist nicht mehr reaktiv.
    fun getEntriesForExercise(exerciseId: String, currentEntries: List<TrainingEntry>): List<TrainingEntry> {
        return currentEntries.filter { it.exerciseId == exerciseId }
    }
}
