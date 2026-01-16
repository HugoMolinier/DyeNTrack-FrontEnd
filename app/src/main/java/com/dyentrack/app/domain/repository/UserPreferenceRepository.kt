package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.UserPreference
import kotlinx.coroutines.flow.StateFlow


interface UserPreferenceRepository {

    val userPreferenceFlow: StateFlow<UserPreference?>
    fun fetchUserPreference()
    fun saveUserPreference(userPreference: UserPreference)
}