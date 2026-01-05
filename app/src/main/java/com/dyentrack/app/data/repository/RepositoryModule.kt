package com.dyentrack.app.data.repository

import com.dyentrack.app.domain.repository.DayDataRepository
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import com.dyentrack.app.domain.repository.MasterDataRepository
import com.dyentrack.app.domain.repository.PresetRepository
import com.dyentrack.app.domain.repository.UserConnectionRepository
import com.dyentrack.app.domain.repository.UserPreferenceRepository
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
    abstract fun bindDayDataRepository(
        impl: DayDataRepositoryImpl
    ): DayDataRepository

    @Binds
    @Singleton
    abstract fun bindUserConnectionRepository(
        impl: UserConnectionRepositoryImpl
    ): UserConnectionRepository


    @Binds
    @Singleton
    abstract fun bindUserPreferenceRepository(
        impl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindPresetRepository(
        impl: PresetRepositoryImpl
    ): PresetRepository


}
