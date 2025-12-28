package de.alex.lightweights.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "training_entries",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
    )

data class TrainingEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val date: LocalDate,
    val weight: Float,
    val reps: Int
)
