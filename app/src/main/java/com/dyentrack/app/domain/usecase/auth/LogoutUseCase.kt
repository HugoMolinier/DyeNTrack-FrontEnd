package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class LogoutUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    operator fun invoke()  {
        repository.logout()
    }
}