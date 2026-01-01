package com.dyentrack.app.domain.usecase.preference

import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.repository.UserPreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveUserPreferenceUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) {
    operator fun invoke(userPreference: UserPreference) {
        userPreferenceRepository.saveUserPreference(userPreference)
    }
}