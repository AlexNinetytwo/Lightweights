package de.alex.lightweights.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.alex.lightweights.LightweightsApp
import de.alex.lightweights.data.ExerciseDataSource
import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrackViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _editingExercise = MutableStateFlow<Exercise?>(null)
    private val _deletingExercise = MutableStateFlow<Exercise?>(null)
    val editingExercise: StateFlow<Exercise?> = _editingExercise
    val deletingExercise: StateFlow<Exercise?> = _deletingExercise

    private val exerciseDataSource =
        ExerciseDataSource(
            (application as LightweightsApp)
                .database
                .exerciseDao()
        )

    val exercises: Flow<List<Exercise>> =
        exerciseDataSource.exercises

    fun startDeletingExercise(exercise: Exercise) {
        _deletingExercise.value = exercise
    }

    fun stopDeletingExercise() {
        _deletingExercise.value = null
    }

    fun startEditing(exercise: Exercise) {
        _editingExercise.value = exercise
    }

    fun stopEditing() {
        _editingExercise.value = null
    }

    fun updateExercise(
        exercise: Exercise,
        name: String
    ) {
        viewModelScope.launch {
            exerciseDataSource.updateExercise(exercise.copy(name = name)
            )
        }
        stopEditing()
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDataSource.deleteExercise(exercise)
        }
        stopDeletingExercise()
    }
}
