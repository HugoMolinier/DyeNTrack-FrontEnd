package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.UserPreference


interface UserPreferenceRepository {
    fun getUserPreference(): UserPreference
    fun saveUserPreference(userPreference: UserPreference)
}