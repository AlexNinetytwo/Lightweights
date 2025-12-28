package de.alex.lightweights.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import de.alex.lightweights.domain.model.TrainingEntry
import de.alex.lightweights.domain.model.Exercise
import java.time.LocalDate

// Konverter, der LocalDate in einen String umwandelt und zurück
class Converters {

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}

@Database(
    entities = [Exercise::class, TrainingEntry::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingEntryDao(): TrainingEntryDao
}
