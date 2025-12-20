package com.dyentrack.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal

@Database(entities = [SeanceOfDayLocal::class], version = 10)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun seanceDao(): SeanceDao
}