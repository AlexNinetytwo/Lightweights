package de.alex.lightweights.domain

import android.util.Log
import de.alex.lightweights.domain.model.TrainingEntry
import kotlin.math.exp

fun calculateMovedWeight(entry: TrainingEntry): Float {
//    return entry.weight * (1f + entry.reps / 30f)
    return entry.weight * entry.reps
}

fun calculateMovedWeight(entry: SummerizedTrainingEntry): Float {
    return entry.weight * entry.reps
}

fun strengthFromReps(x: Int, maxReps: Double, cutoff: Double): Double {
    return maxReps * (1.0 - exp(-cutoff * x))
}

fun calculateStrength(
    weight: Float,
    reps: Int,
    maxReps: Double,
    cutoff: Double
): Double {
    return weight * strengthFromReps(reps, maxReps, cutoff)
}
