package com.dyentrack.app.domain.usecase.auth

import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.repository.UserConnectionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateUserUseCase @Inject constructor(
    private val repository: UserConnectionRepository
) {
    suspend operator fun invoke(
        pseudo: String,
        height: Int?,
        sexe: Boolean?,
        birthDate: String?
    ): RequestState {
        return repository.updateUser(pseudo, height, sexe, birthDate)
    }
}