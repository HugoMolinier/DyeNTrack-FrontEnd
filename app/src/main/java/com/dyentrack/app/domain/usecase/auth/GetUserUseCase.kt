package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.User
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    suspend operator fun invoke(): User? {
        return repository.getUser()
    }
}