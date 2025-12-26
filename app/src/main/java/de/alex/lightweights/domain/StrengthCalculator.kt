package de.alex.lightweights.domain

fun calculateStrenght(entry: TrainingEntry): Float {
    return entry.weightKg * (1f + entry.reps / 30f)
}
