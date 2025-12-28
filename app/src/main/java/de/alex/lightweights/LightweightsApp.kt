package de.alex.lightweights
import de.alex.lightweights.data.AppDatabase
import android.app.Application
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase
import de.alex.lightweights.domain.model.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LightweightsApp: Application() {
    // Lazy initialisiert die DB, sodass sie erst bei der ersten Verwendung erstellt wird
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "lightweights-db"
        ).addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    CoroutineScope(Dispatchers.IO).launch {
                        seedExercises()
                    }
                }
            }
        ).fallbackToDestructiveMigration().build()
    }

    private suspend fun seedExercises() {
        val dao = database.exerciseDao()

        val defaultExercises = listOf(
            "Bankdrücken",
            "Kniebeugen",
            "Kreuzheben",
            "Schulterdrücken",
            "Rudern",
            "Latzug",
            "Bizepscurls",
            "Trizepsdrücken",
            "Beinpresse",
            "Wadenheben"
        )

        defaultExercises.forEach { name ->
            dao.insert(Exercise(name = name))
        }
    }
}
