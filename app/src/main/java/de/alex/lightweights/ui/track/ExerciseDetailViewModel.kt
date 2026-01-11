package de.alex.lightweights.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.alex.lightweights.LightweightsApp
import de.alex.lightweights.data.TrainingEntryDataSource
import de.alex.lightweights.domain.model.TrainingEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExerciseDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val trainingEntryDataSource =
        TrainingEntryDataSource(
            (application as LightweightsApp)
                .database
                .trainingEntryDao()
        )

    val entries: Flow<List<TrainingEntry>> =
        trainingEntryDataSource.entries

    suspend fun addTrainingEntry(
        exerciseId: String,
        weight: Float,
        reps: Int,
        date: LocalDate
    ) {
        val entry = TrainingEntry(
            exerciseId = exerciseId,
            date = date,
            weight = weight,
            reps = reps
        )
        trainingEntryDataSource.addEntry(entry)
    }

    fun updateTrainingEntry(
        entry: TrainingEntry,
        newWeight: Float,
        newReps: Int
    ) {
        viewModelScope.launch {
            trainingEntryDataSource.updateEntry(
                entry.copy(
                    weight = newWeight,
                    reps = newReps
                )
            )
        }
    }

    fun deleteTrainingEntry(entry: TrainingEntry) {
        viewModelScope.launch {
            trainingEntryDataSource.deleteEntry(entry)
        }
    }
}
