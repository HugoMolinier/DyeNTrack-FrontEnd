package com.dyentrack.app.domain.usecase.infoExerciseUser

import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import javax.inject.Inject

class SaveInfoExerciseUserUseCase @Inject constructor(
    private val repository: InfoExerciseUserRepository
) {
    suspend operator fun invoke(infoExerciseUser: InfoExerciseUser): InfoExerciseUser {
        return repository.saveInfoExerciseUser(infoExerciseUser)
    }
}