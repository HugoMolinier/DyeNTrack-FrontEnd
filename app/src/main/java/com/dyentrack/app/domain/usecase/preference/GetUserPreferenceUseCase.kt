package com.dyentrack.app.domain.usecase.preference

import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchUserPreferenceUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) {
    val userPreferenceFlow: Flow<UserPreference?> = userPreferenceRepository.userPreferenceFlow
    operator fun invoke() {
        return userPreferenceRepository.fetchUserPreference()
    }
}