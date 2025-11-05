package com.dyentrack.app.domain.usecase.infoExerciseUser


import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import javax.inject.Inject

class GetAllInfoExerciseUserUseCase @Inject constructor(
    private val repository: InfoExerciseUserRepository
) {
    suspend operator fun invoke(): List<InfoExerciseUser> {
        return repository.getInfoExerciseUser()
    }
}