package com.dyentrack.app.domain.usecase

import com.dyentrack.app.domain.model.DayInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

class GetDaysRangeUseCase @Inject constructor() {

    fun getLast30Days(): List<DayInfo> {
        val today = LocalDate.now().plusDays(3)
        return (0 until 30).map {
            val date = today.minusDays(it.toLong())
            date.toDayInfo()
        }
    }

    /**
     * Étend la liste existante vers le passé jusqu'à newMinDate.
     * Si newMinDate est déjà inclus dans existingDays, rien n'est ajouté.
     */
    fun extendDaysIfNeeded(existingDays: Set<DayInfo>, newMinDate: LocalDate): List<DayInfo> {
        if (existingDays.isEmpty()) return emptyList()

        val currentMin = LocalDate.parse(existingDays.first().dateFormat)
        if (!newMinDate.isBefore(currentMin)) return emptyList() // déjà couvert

        val daysBetween = ChronoUnit.DAYS.between(newMinDate, currentMin).toInt()
        return (0 until daysBetween).map { i ->
            val date = newMinDate.plusDays(i.toLong())
            date.toDayInfo()
        }.reversed()
    }

    private fun LocalDate.toDayInfo(): DayInfo {
        return DayInfo(
            shortDay = this.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.FRENCH),
            dayNumber = this.dayOfMonth,
            month = this.month.getDisplayName(TextStyle.SHORT, Locale.FRENCH),
            dateFormat = this.format(DateTimeFormatter.ISO_DATE)
        )
    }
}
