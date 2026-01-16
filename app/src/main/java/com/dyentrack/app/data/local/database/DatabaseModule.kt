package com.dyentrack.app.data.local.database


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "dyentrack_db"
        ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    @Singleton
    fun provideSeanceDao(database: AppDatabase): SeanceDao {
        return database.seanceDao()
    }

    @Provides
    @Singleton
    fun providePresetDao(database: AppDatabase): PresetDao {
        return database.presetDao()
    }
}
