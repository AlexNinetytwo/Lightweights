package de.alex.lightweights.data

import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

class ExerciseDataSource(
    private val dao: ExerciseDao
) {

    val exercises: Flow<List<Exercise>> =
        dao.getAllExercises()

    suspend fun addExercise(name: String) {
        dao.insert(
            Exercise(name = name)
        )
    }

    suspend fun updateExercise(exercise: Exercise) {
        dao.update(exercise)
    }


    suspend fun deleteExercise(exercise: Exercise) {
        dao.delete(exercise)
    }

    companion object
}
