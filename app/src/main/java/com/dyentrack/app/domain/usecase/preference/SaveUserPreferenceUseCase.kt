package com.dyentrack.app.domain.usecase.preference

import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.repository.UserPreferenceRepository
import javax.inject.Inject

class SaveUserPreferenceUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) {
    operator fun invoke(userPreference: UserPreference) {
        userPreferenceRepository.saveUserPreference(userPreference)
    }
}