package de.alex.lightweights.ui.track

import androidx.lifecycle.ViewModel
import de.alex.lightweights.data.ExerciseDataSource

class AddExerciseViewModel : ViewModel() {

    fun addExercise(name: String) {
        ExerciseDataSource.addExercise(name)
    }
}
