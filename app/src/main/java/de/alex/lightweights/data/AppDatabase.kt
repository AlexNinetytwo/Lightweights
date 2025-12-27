package de.alex.lightweights.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import de.alex.lightweights.domain.model.TrainingEntry
import java.time.LocalDate

// Konverter, der LocalDate in einen String umwandelt und zurück
class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}

@Database(entities = [TrainingEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingEntryDao(): TrainingEntryDao
}
