package com.dyentrack.app.presentation.utils

import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetVO
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