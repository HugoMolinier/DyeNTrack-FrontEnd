package com.dyentrack.app.domain.model

import java.time.Instant


data class Token(
    val token: String, val expireIn: Int, val tokenType: String, val dateRequest: Instant? = null
)
