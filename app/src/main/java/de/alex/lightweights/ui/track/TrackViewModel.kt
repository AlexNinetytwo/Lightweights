package de.alex.lightweights.ui.track

import androidx.lifecycle.ViewModel
import de.alex.lightweights.data.ExerciseDataSource
import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.flow.StateFlow

class TrackViewModel : ViewModel() {

    val exercises: StateFlow<List<Exercise>> =
        ExerciseDataSource.exercises
}

