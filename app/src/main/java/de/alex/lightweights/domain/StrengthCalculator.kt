import de.alex.lightweights.domain.model.TrainingEntry

fun calculateStrength(entry: TrainingEntry): Float {
//    return entry.weight * (1f + entry.reps / 30f)
    return entry.weight * entry.reps
}
