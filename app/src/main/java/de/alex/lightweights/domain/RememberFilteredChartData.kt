package de.alex.lightweights.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import de.alex.lightweights.domain.model.TrainingEntry
import de.alex.lightweights.data.DailyExerciseStats
import androidx.compose.runtime.State
import de.alex.lightweights.data.SummarizedDailyExerciseStats
import java.time.LocalDate
import kotlin.collections.component1
import kotlin.collections.component2


enum class TimeFilter { ALL, YEAR, QUARTER, MONTH }

@Composable
fun rememberFilteredChartData(
    entries: List<TrainingEntry>,
    exerciseId: String,
    selectedFilter: TimeFilter,
    maxReps: Double,
    cutoff: Double
): State<List<DailyExerciseStats>> {
    return remember(entries, selectedFilter, exerciseId, maxReps, cutoff) {
        derivedStateOf {
            val dailyAggregatedData: List<DailyExerciseStats> =
                entries
                    .filter { it.exerciseId == exerciseId }
                    .groupBy { it.date }
                    .map { (date, dayEntries) ->
                        val movedWeight = dayEntries
                            .sumOf { calculateMovedWeight(it).toDouble() }
                            .toFloat()

                        val bestStrength = dayEntries
                            .maxOf { calculateStrength(it.weight, it.reps, maxReps, cutoff) }

                        DailyExerciseStats(
                            date = date,
                            movedWeight = movedWeight,
                            bestStrength = bestStrength
                        )
                    }
                    .sortedBy { it.date }

            if (dailyAggregatedData.isEmpty()) {
                return@derivedStateOf emptyList()
            }

            val today = LocalDate.now()
            val limitDate = when (selectedFilter) {
                TimeFilter.YEAR -> today.minusYears(1)
                TimeFilter.QUARTER -> today.minusMonths(3)
                TimeFilter.MONTH -> today.minusMonths(1)
                TimeFilter.ALL -> null // Kein Zeitlimit
            }

            if (limitDate != null) {
                dailyAggregatedData.filter { (date, _) -> date.isAfter(limitDate) }
            } else {
                dailyAggregatedData
            }
        }
    }
}

@Composable
fun rememberFilteredChartDataForSummerizedExerciseStats(
    entries: List<TrainingEntry>,
    selectedFilter: TimeFilter,
    maxReps: Double,
    cutoff: Double
): State<List<SummarizedDailyExerciseStats>> {
    // 1. Die gesamte Logik in remember und derivedStateOf einschließen
    return remember(entries, selectedFilter, maxReps, cutoff) {
        derivedStateOf {
            val allTrainingDates = entries
                .map { it.date }
                .distinct()
                .sorted()

            if (allTrainingDates.isEmpty()) {
                // Früher Ausstieg, wenn keine Einträge vorhanden sind
                return@derivedStateOf emptyList<SummarizedDailyExerciseStats>()
            }

            val lastKnownExerciseState = mutableMapOf<String, ExerciseState>()
            val dailyStats = mutableListOf<SummarizedDailyExerciseStats>()

            for (date in allTrainingDates) {
                val entriesOfDay = entries.filter { it.date == date }

                // 🔄 Update states mit heutigen Einträgen
                for (entry in entriesOfDay) {
                    lastKnownExerciseState[entry.exerciseId] = ExerciseState(
                        weight = entry.weight,
                        reps = entry.reps
                    )
                }

                // 📦 Volumen: nur heutige Einträge
                val movedWeight = entriesOfDay
                    .sumOf { calculateMovedWeight(it).toDouble() }
                    .toFloat()

                // 💪 Stärke: alle bekannten Übungen
                val strength = lastKnownExerciseState.values
                    .sumOf { state ->
                        calculateStrength(
                            weight = state.weight,
                            reps = state.reps,
                            maxReps = maxReps,
                            cutoff = cutoff
                        )
                    }

                // 2. Den korrekten Typ 'SummarizedDailyExerciseStats' verwenden
                dailyStats += SummarizedDailyExerciseStats(
                    date = date,
                    movedWeight = movedWeight,
                    bestStrength = strength
                )
            }

            // --- Zeitfilterung ---
            val today = LocalDate.now()
            val limitDate = when (selectedFilter) {
                TimeFilter.YEAR -> today.minusYears(1)
                TimeFilter.QUARTER -> today.minusMonths(3)
                TimeFilter.MONTH -> today.minusMonths(1)
                TimeFilter.ALL -> null // Kein Zeitlimit
            }

            // 3. Das Ergebnis des Blocks explizit zurückgeben
            if (limitDate != null) {
                dailyStats.filter { it.date.isAfter(limitDate) }
            } else {
                dailyStats
            }
        }
    }
}

@Composable
fun summerizeTrainingEntries(
    entries: List<TrainingEntry>
): List<SummerizedTrainingEntry> {
    return entries
        .groupBy { it.date } // Groups entries into a Map<Date, List<Entry>>
        .map { (date, entriesForDate) -> // Iterates over the map
            SummerizedTrainingEntry(
                date = date,
                weight = entriesForDate.sumOf { it.weight.toDouble() }.toFloat(),
                reps = entriesForDate.sumOf { it.reps }
            )
        }
}

data class ExerciseState(
    val weight: Float,
    val reps: Int
)


