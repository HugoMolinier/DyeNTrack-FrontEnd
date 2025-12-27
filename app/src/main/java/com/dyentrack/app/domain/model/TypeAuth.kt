package com.dyentrack.app.domain.model

enum class AuthError {
    ERROR_API,
    INVALID_ARGUMENT,
    NOT_EMAILFORMAT,
    NOT_PSEUDOFORMAT,
    NOT_HEIGHTFORMAT,
    UNAUTHORIZED,
    NOT_PASSWORD
}
enum class AuthStatus {
    CONNECTED,
    NOT_CONNECTED
}
data class AuthState(
    val isConnected: Boolean,
    val user: User?
)

sealed class RequestState {
    object Idle : RequestState()
    object Loading : RequestState()
    object Success : RequestState()
    data class Error(val error: AuthError) : RequestState()
}