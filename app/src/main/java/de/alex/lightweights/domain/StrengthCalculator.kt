package de.alex.lightweights.domain

fun calculateStrength(entry: TrainingEntry): Float {
//    return entry.weightKg * (1f + entry.reps / 30f)
    return entry.weightKg * entry.reps
}
