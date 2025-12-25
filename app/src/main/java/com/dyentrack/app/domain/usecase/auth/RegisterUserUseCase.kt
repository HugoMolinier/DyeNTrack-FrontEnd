package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.ResponseAuth
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    suspend operator fun invoke(email: String, password: String,pseudo:String): ResponseAuth {
        return repository.registerUser(email, password,pseudo)
    }
}
