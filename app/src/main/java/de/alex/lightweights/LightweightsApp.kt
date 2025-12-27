package de.alex.lightweights
import de.alex.lightweights.data.AppDatabase
import android.app.Application
import androidx.room.Room

class LightweightsApp: Application() {
    // Lazy initialisiert die DB, sodass sie erst bei der ersten Verwendung erstellt wird
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "lightweights-db"
        ).build()
    }
}
