package de.alex.lightweights.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.alex.lightweights.LightweightsApp
import de.alex.lightweights.data.TrainingEntryDataSource
import de.alex.lightweights.domain.model.TrainingEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

//    Pausentimer

    private var timerJob: Job? = null

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun startPause(seconds: Int) {
        timerJob?.cancel()

        _remainingSeconds.value = seconds
        _isRunning.value = true

        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1_000)
                _remainingSeconds.value--
            }
            _isRunning.value = false
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resetTimer() {
        timerJob?.cancel()
        _remainingSeconds.value = 0
        _isRunning.value = false
    }
}
