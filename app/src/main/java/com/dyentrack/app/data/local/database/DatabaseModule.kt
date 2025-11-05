package com.dyentrack.app.data.local.database


import android.content.Context
import androidx.room.Room

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dyentrack_db"
        ).fallbackToDestructiveMigration() .build()
    }

    @Provides
    fun provideSeanceDao(database: AppDatabase): SeanceDao {
        return database.seanceDao()
    }
}
