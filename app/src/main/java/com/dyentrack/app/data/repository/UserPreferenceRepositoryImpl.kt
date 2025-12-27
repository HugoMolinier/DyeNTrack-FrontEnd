package com.dyentrack.app.data.repository

import com.dyentrack.app.data.local.datasource.LocalDataSource
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.repository.UserPreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepositoryImpl  @Inject constructor(
    private val localDataSource: LocalDataSource
) : UserPreferenceRepository {

    override fun getUserPreference(): UserPreference {
        return localDataSource.getUserPreference() ?: UserPreference().also {
            localDataSource.saveUserPreference(it)
        }
    }
    override fun saveUserPreference(userPreference: UserPreference) {
        val localPref = localDataSource.getUserPreference()

        // garde la valeur locale si  null
        val mergedPref = UserPreference(
            autoSync = userPreference.autoSync ?: localPref?.autoSync ?: true,
            objCalorie = userPreference.objCalorie ?: localPref?.objCalorie,
            objStep = userPreference.objStep ?: localPref?.objStep,
            objHourSleep = userPreference.objHourSleep ?: localPref?.objHourSleep
        )
        localDataSource.saveUserPreference(mergedPref)
    }
}