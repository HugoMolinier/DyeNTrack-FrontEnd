package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.AuthState
import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserConnectionRepository {

    val authState: StateFlow<AuthState>

    fun getUser(): User?
    fun getToken(): String
    fun logout()


    suspend fun loginUser(
        email: String,
        password: String
    ): RequestState

    suspend fun registerUser(
        email: String,
        password: String,
        pseudo: String
    ): RequestState

    suspend fun updateUser(
        pseudo: String,
        height: Int?,
        sexe: Boolean?,
        birthDate: String?
    ): RequestState
}