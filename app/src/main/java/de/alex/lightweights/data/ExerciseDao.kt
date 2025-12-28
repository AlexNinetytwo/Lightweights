package de.alex.lightweights.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}
