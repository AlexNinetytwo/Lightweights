package de.alex.lightweights.ui.track

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import de.alex.lightweights.data.TrainingEntryDataSource
import de.alex.lightweights.domain.model.TrainingEntry
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class ExerciseDetailViewModel : ViewModel() {

    val entries: StateFlow<List<TrainingEntry>> =
        TrainingEntryDataSource.entries

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTrainingEntry(
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
        TrainingEntryDataSource.addEntry(entry)
    }
}
