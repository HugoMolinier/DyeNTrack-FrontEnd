package com.dyentrack.app.domain.usecase

import com.dyentrack.app.domain.model.DayInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class GetLast30DaysUseCase @Inject constructor() {
    operator fun invoke(): List<DayInfo> {
        val today = LocalDate.now().plusDays(3)
        return (0 until 30).map {
            val date = today.minusDays(it.toLong())
            DayInfo(
                shortDay = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.FRENCH),
                dayNumber = date.dayOfMonth,
                month = date.month.getDisplayName(TextStyle.SHORT,Locale.FRENCH),
                dateFormat = date.format(DateTimeFormatter.ISO_DATE)
            )
        }
    }
}
