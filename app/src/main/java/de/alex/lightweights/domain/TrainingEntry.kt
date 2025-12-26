package de.alex.lightweights.domain

import java.time.LocalDate

data class TrainingEntry(
    val exerciseId: String,
    val date: LocalDate,
    val weightKg: Float,
    val reps: Int
)
