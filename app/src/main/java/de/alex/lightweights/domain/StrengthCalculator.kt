package de.alex.lightweights.domain

import de.alex.lightweights.domain.model.TrainingEntry
import kotlin.math.exp

fun calculateMovedWeight(entry: TrainingEntry): Float {
//    return entry.weight * (1f + entry.reps / 30f)
    return entry.weight * entry.reps
}

fun strengthFromReps(x: Int, maxReps: Double, cutoff: Double): Double {
    return maxReps * (1.0 - exp(-cutoff * x))
}

fun calculateStrength(entry: TrainingEntry, maxReps: Double, cutoff: Double): Double {
    return entry.weight * strengthFromReps(entry.reps, maxReps, cutoff)
}
