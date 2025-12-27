package com.dyentrack.app.data.local.datasource

import com.dyentrack.app.data.local.entities.SessionLocal
import com.dyentrack.app.data.local.entities.UserLocal
import com.dyentrack.app.domain.model.Token
import com.dyentrack.app.domain.model.User

fun User.toLocal() = UserLocal(
    id = id,
    pseudo = pseudo,
    dateRegister = dateRegister,
    birthdate = birthdate,
    height = height,
    sexe = sexeMale
)

fun UserLocal.toDomain() = User(
    id = id,
    pseudo = pseudo,
    dateRegister = dateRegister,
    birthdate = birthdate,
    height = height,
    sexeMale = sexe
)

fun Token.toLocal(): SessionLocal {
    val expirationTime = System.currentTimeMillis() + expireIn * 1000L
    return SessionLocal(
        token = token,
        tokenType = tokenType,
        expirationTime = expirationTime,

    )
}