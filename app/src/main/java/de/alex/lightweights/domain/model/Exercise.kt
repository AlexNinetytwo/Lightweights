package de.alex.lightweights.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String
)
