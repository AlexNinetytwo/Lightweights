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

    suspend fun updateEntry(entry: TrainingEntry) =
        dao.updateEntry(entry)

    suspend fun deleteEntry(entry: TrainingEntry) =
        dao.deleteEntry(entry)
}
