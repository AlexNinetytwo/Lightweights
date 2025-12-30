package de.alex.lightweights.domain

import java.time.LocalDate
import java.util.UUID

data class SummerizedTrainingEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val weight: Float,
    val reps: Int
)