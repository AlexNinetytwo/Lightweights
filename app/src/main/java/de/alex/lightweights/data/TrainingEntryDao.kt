package de.alex.lightweights.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.alex.lightweights.domain.model.TrainingEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingEntryDao {
    @Query("SELECT * FROM training_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<TrainingEntry>> // Flow wird automatisch aktualisiert!

    @Insert
    suspend fun addEntry(entry: TrainingEntry)

    @Update
    suspend fun updateEntry(entry: TrainingEntry)

    @Delete
    suspend fun deleteEntry(entry: TrainingEntry)
}
