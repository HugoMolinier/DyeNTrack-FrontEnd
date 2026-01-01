package com.dyentrack.app.presentation.utils

import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetVO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.toCleanString(): String =
    toInt().takeIf { it.toDouble() == this }?.toString() ?: toString()


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

fun LocalDate.toAbbreviatedDate(userLangue : LANGUAGE): String {
    val locale = when(userLangue) {
        LANGUAGE.FR -> Locale.FRENCH
        LANGUAGE.EN -> Locale.ENGLISH
    }

    return this.format(DateTimeFormatter.ofPattern("d MMM", locale))
}

fun Int?.isNullOrZero() = this == null || this == 0
fun Float?.isNullOrZero() = this == null || this == 0f
fun Double?.isNullOrZero() = this == null || this == 0.0

fun String.normalize() =
    lowercase()
        .replace("é","e")
        .replace("è","e")
        .replace("ê","e")



fun SetVO.toSetData(linkedRightSet: SetData? = null): SetData {
    return SetData(
        type = this.typeOfSet,
        charge = this.charge,
        rir = this.rir,
        reps = this.repsNumber,
        side = this.side,
        linkedRightSet = linkedRightSet
    )
}