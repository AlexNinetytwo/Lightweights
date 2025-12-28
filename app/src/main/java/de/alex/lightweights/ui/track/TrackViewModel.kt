package de.alex.lightweights.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.alex.lightweights.LightweightsApp
import de.alex.lightweights.data.ExerciseDataSource
import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TrackViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val exerciseDataSource =
        ExerciseDataSource(
            (application as LightweightsApp)
                .database
                .exerciseDao()
        )

    val exercises: Flow<List<Exercise>> =
        exerciseDataSource.exercises

    fun updateExercise(
        exercise: Exercise,
        name: String
    ) {
        viewModelScope.launch {
            exerciseDataSource.updateExercise(exercise.copy(name = name)
            )
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDataSource.deleteExercise(exercise)
        }
    }
}
