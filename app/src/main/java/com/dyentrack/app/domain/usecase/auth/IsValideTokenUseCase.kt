package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Inject

class IsValideTokenUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    operator fun invoke(): AuthStatus {
        return repository.isValideToken()
    }
}