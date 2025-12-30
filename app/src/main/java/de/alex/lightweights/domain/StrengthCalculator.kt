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

fun calculateStrength(entry: TrainingEntry, maxReps: Double, cutoff: Double): Double {
    val factor = strengthFromReps(entry.reps, maxReps, cutoff)
//    Log.d("calc strength", "weight: ${entry.weight}, reps: ${entry.reps}, factor: $factor")
//    Log.d("calc strength", "entry: ${entry.date}, strength: ${entry.weight * factor}, moved weight: ${calculateMovedWeight(entry)}")
    return entry.weight * factor * 0.1
}

fun calculateStrength(entry: SummerizedTrainingEntry, maxReps: Double, cutoff: Double): Double {
    return entry.weight * strengthFromReps(entry.reps, maxReps, cutoff) * 0.1
}
