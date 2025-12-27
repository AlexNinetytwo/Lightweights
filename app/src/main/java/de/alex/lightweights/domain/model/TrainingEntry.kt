package de.alex.lightweights.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "training_entries")
data class TrainingEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val date: LocalDate,
    val weight: Float,
    val reps: Int
)
