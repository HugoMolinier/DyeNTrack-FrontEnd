package com.dyentrack.app.domain.usecase.infoExerciseUser

import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateInfoExerciseUserUseCase @Inject constructor(
    private val repository: InfoExerciseUserRepository
) {
    suspend operator fun invoke(infoExerciseUser: InfoExerciseUser): Int {
        return repository.updateInfoExerciseUser(infoExerciseUser)
    }
}