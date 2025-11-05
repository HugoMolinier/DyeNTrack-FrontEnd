package com.dyentrack.app.data.repository

import com.dyentrack.app.domain.repository.APISeanceRepository
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import com.dyentrack.app.domain.repository.LocalSeanceRepository
import com.dyentrack.app.domain.repository.MasterDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMasterDataRepository(
        impl: MasterDataRepositoryImpl
    ): MasterDataRepository

    @Binds
    @Singleton
    abstract fun bindInfoExerciseUserRepository(
        impl: InfoExerciseUserRepositoryImpl
    ): InfoExerciseUserRepository

    @Binds
    @Singleton
    abstract fun bindAPISeanceRepository(
        impl: APISeanceRepositoryImpl
    ): APISeanceRepository

    @Binds
    @Singleton
    abstract fun bindLocalSeanceRepository(
        impl: LocalSeanceRepositoryImpl
    ): LocalSeanceRepository
}
