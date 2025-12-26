package de.alex.lightweights.data

import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ExerciseDataSource {

    private val _exercises = MutableStateFlow<List<Exercise>>(
        listOf(
            Exercise(name = "Bankdrücken"),
            Exercise(name = "Kniebeugen"),
            Exercise(name = "Rudern"),
            Exercise(name = "Schulterdrücken")
        )
    )

    val exercises: StateFlow<List<Exercise>> = _exercises

    fun addExercise(name: String) {
        _exercises.value = _exercises.value + Exercise(name = name)
    }

    fun getById(id: String): Exercise? = _exercises.value.find { it.id == id }
}
