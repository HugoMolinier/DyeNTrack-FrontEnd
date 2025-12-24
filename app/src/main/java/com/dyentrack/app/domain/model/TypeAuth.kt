package com.dyentrack.app.domain.model

enum class AuthError {
    ERROR_API,
    INVALID_ARGUMENT,
    NOT_EMAILFORMAT,
    UNAUTHORIZED,
    NOT_PASSWORD
}
enum class AuthStatus {
    CONNECTED,
    NOT_CONNECTED
}
