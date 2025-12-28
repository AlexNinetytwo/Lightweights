package de.alex.lightweights.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.alex.lightweights.LightweightsApp
import de.alex.lightweights.data.ExerciseDataSource
import kotlinx.coroutines.launch

class AddExerciseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val exerciseDataSource =
        ExerciseDataSource(
            (application as LightweightsApp)
                .database
                .exerciseDao()
        )

    fun addExercise(name: String) {
        viewModelScope.launch {
            exerciseDataSource.addExercise(name)
        }
    }
}
