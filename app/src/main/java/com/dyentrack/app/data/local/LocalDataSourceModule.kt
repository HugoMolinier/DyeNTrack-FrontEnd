package com.dyentrack.app.data.local

import com.dyentrack.app.data.local.datasource.FileLocalDataSource
import com.dyentrack.app.data.local.datasource.LocalDataSource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        fileLocalDataSource: FileLocalDataSource
    ): LocalDataSource



}