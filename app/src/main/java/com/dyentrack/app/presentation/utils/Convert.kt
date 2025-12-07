package com.dyentrack.app.presentation.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.toCleanString(): String =
    toInt().takeIf { it.toDouble() == this }?.toString() ?: toString()



private val SLASH_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val ABBREVIATED_FORMATTER =
    DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH)

fun LocalDate.toSlashDate(): String {
    return this.format(SLASH_FORMATTER)
}

fun LocalDate.toIsoDate(): String {
    return this.format(ISO_FORMATTER)
}

fun LocalDate.toAbbreviatedDate(): String {
    return this.format(ABBREVIATED_FORMATTER)
}