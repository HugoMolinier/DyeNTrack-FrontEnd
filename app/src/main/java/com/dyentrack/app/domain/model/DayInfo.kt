package com.dyentrack.app.domain.model

import java.time.LocalDate

data class DayInfo(
    val shortDay: String,
    val dayNumber: Int,
    val month: String,
    val dateFormat: String,
    val dateLocal: LocalDate
)
