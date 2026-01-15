package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteUserUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    suspend operator fun invoke(): RequestState {
        return repository.deleteAll()
    }
}