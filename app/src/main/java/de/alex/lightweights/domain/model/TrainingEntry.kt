package de.alex.lightweights.domain.model

import java.time.LocalDate
import java.util.UUID

data class TrainingEntry(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val date: LocalDate,
    val weight: Float,
    val reps: Int
)
