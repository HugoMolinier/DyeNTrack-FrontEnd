package com.dyentrack.app.domain.model

data class ResponseAuth(
    val userDTO: User? ,
    val token: Token?,
    val status: AuthStatus,
    val error: AuthError?

)
