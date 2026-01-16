package com.dyentrack.app.domain.model


data class User(
    val id: Long,
    val pseudo: String,
    val dateRegister: String,
    val birthdate: String? = null,
    val height: Int? = null,
    val sexeMale: Boolean? = null
)
