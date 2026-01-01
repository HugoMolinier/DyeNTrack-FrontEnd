package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class RegisterUserUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    suspend operator fun invoke(email: String, password: String,pseudo:String): RequestState {
        return repository.registerUser(email, password,pseudo)
    }
}
