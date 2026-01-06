package com.dyentrack.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.preset.Preset

@Database(entities = [DayDataRequest::class, Preset::class], version = 14)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun seanceDao(): SeanceDao

    abstract fun presetDao(): PresetDao
}