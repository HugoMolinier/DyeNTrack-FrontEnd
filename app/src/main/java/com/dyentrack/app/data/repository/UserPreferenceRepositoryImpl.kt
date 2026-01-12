package com.dyentrack.app.data.repository

import com.dyentrack.app.data.local.datasource.LocalDataSource
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.model.enums.SyncPerformanceMode
import com.dyentrack.app.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : UserPreferenceRepository {

    private val _userPreferenceFlow = MutableStateFlow<UserPreference?>(null)

    // Flow exposé aux ViewModels
    override val userPreferenceFlow: StateFlow<UserPreference?> get() = _userPreferenceFlow

    override fun fetchUserPreference() {
        _userPreferenceFlow.value = localDataSource.getUserPreference()
    }

    override fun saveUserPreference(userPreference: UserPreference) {
        val localPref = localDataSource.getUserPreference()

        // garde la valeur locale si  null
        val mergedPref = UserPreference(
            autoSync = userPreference.autoSync ?: localPref?.autoSync ?: true,
            objCalorie = userPreference.objCalorie ?: localPref?.objCalorie,
            objStep = userPreference.objStep ?: localPref?.objStep,
            objHourSleep = userPreference.objHourSleep ?: localPref?.objHourSleep,
            mesureUnit = userPreference.mesureUnit ?: localPref?.mesureUnit ?: MesureUnit.KG,
            language = userPreference.language ?: localPref?.language ?: LANGUAGE.FR,
            syncPerformanceMode = userPreference.syncPerformanceMode
                ?: localPref?.syncPerformanceMode ?: SyncPerformanceMode.ALL
        )
        localDataSource.saveUserPreference(mergedPref)
        _userPreferenceFlow.value = mergedPref

    }
}