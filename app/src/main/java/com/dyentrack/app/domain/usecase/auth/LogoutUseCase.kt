package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.ResponseAuth
import com.dyentrack.app.domain.model.User
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    operator fun invoke(): ResponseAuth  {
        return repository.logout()
    }
}