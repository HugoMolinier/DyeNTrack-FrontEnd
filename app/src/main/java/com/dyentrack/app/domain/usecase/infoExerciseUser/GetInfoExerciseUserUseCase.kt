package com.dyentrack.app.domain.usecase.infoExerciseUser


import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInfoExerciseUserUseCase @Inject constructor(
    private val repository: InfoExerciseUserRepository
) {
    val infoExerciseUserFlow: StateFlow<Map<Long, InfoExerciseUser>> = repository.infoExerciseUserFlow
    suspend operator fun invoke() {
        return repository.fetchInfoExerciseUser()
    }
}