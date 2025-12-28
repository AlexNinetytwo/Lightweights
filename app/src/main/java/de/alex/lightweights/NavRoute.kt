package de.alex.lightweights

sealed class NavRoute(val route: String) {

    object Track : NavRoute("track")
    object Progress: NavRoute("progress")

    object ExerciseDetail: NavRoute("detail/{exerciseId}/{exerciseName}") {
        fun createRoute(id: String, name: String) =
            "detail/$id/$name"
    }

    object AddExercise : NavRoute("addExercise")
}