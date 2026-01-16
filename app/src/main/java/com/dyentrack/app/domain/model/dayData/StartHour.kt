package com.dyentrack.app.domain.model.dayData

import kotlinx.serialization.Serializable

@Serializable
data class StartHour(
    val hour: Int,
    val minute: Int,
    val second: Int?,
    val nano: Int?
)