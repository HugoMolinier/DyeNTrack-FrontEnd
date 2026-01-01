package com.dyentrack.app.domain.usecase

import com.dyentrack.app.domain.model.DayInfo
import com.dyentrack.app.domain.model.LANGUAGE
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

class GetDaysRangeUseCase @Inject constructor() {

    fun getLast30Days(userLangue: LANGUAGE): List<DayInfo> {
        val today = LocalDate.now().plusDays(3)
        return (0 until 30).map {
            val date = today.minusDays(it.toLong())
            date.toDayInfo(userLangue)
        }
    }

    /**
     * Étend la liste existante vers le passé jusqu'à newMinDate.
     * Si newMinDate est déjà inclus dans existingDays, rien n'est ajouté.
     */
    fun extendDaysIfNeeded(existingDays: Set<DayInfo>, newMinDate: LocalDate,userLangue: LANGUAGE): List<DayInfo> {
        if (existingDays.isEmpty()) return emptyList()

        val currentMin = LocalDate.parse(existingDays.first().dateFormat)
        if (!newMinDate.isBefore(currentMin)) return emptyList() // déjà couvert

        val daysBetween = ChronoUnit.DAYS.between(newMinDate, currentMin).toInt()
        return (0 until daysBetween).map { i ->
            val date = newMinDate.plusDays(i.toLong())
            date.toDayInfo(userLangue)
        }.reversed()
    }

    private fun LocalDate.toDayInfo(userLangue: LANGUAGE): DayInfo {
        val locale = when(userLangue) {
            LANGUAGE.FR -> Locale.FRENCH
            LANGUAGE.EN -> Locale.ENGLISH
        }

        return DayInfo(
            shortDay = this.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
            dayNumber = this.dayOfMonth,
            month = this.month.getDisplayName(TextStyle.SHORT, locale),
            dateFormat = this.format(DateTimeFormatter.ISO_DATE)
        )
    }
}
