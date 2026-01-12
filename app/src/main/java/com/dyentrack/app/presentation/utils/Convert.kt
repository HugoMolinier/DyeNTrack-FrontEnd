package com.dyentrack.app.presentation.utils

import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MesureUnit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


fun convertWeight(value: Double?, sourceUnit: MesureUnit, finalUnit: MesureUnit): Double? {
    if (value == null) return null
    return when {
        sourceUnit == finalUnit -> value
        sourceUnit == MesureUnit.KG && finalUnit == MesureUnit.LBS -> value * 2.20462262185
        sourceUnit == MesureUnit.LBS && finalUnit == MesureUnit.KG -> value * 0.45359237
        else -> value
    }
}

fun convertGrammeOz(value: Double?, sourceUnit: MesureUnit, finalUnit: MesureUnit): Double? {
    if (value == null) return null
    return when {
        sourceUnit == finalUnit -> value
        sourceUnit == MesureUnit.KG && finalUnit == MesureUnit.LBS -> value / 28.34952
        sourceUnit == MesureUnit.LBS && finalUnit == MesureUnit.KG -> value * 28.34952
        else -> value
    }
}

fun LocalDate.toIsoDate(): String = this.format(DateTimeFormatter.ISO_DATE)
fun LocalDate.toAbbreviatedDate(userLangue: LANGUAGE): String {
    val locale = when (userLangue) {
        LANGUAGE.FR -> Locale.FRENCH
        LANGUAGE.EN -> Locale.ENGLISH
    }

    return this.format(DateTimeFormatter.ofPattern("d MMM", locale))
}



