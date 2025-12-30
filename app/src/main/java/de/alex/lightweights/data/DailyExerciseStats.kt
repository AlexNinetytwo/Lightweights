package de.alex.lightweights.data

import java.time.LocalDate

data class DailyExerciseStats(
    val date: LocalDate,
    val movedWeight: Float,
    val bestStrength: Double
)

typealias SummarizedDailyExerciseStats = DailyExerciseStats
