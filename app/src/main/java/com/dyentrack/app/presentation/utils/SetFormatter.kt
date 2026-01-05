package com.dyentrack.app.presentation.utils

import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetSide

fun List<SetData>?.formatForDisplay(userUnit: MesureUnit): String {
    if (this.isNullOrEmpty()) return ""
    val sb = StringBuilder()
    var previousCharge: Double? = null
    var stop = false
    for (set in this) {
        if (stop) break
        if (previousCharge != null && set.charge != previousCharge) {
            sb.append(
                convertWeight(previousCharge, MesureUnit.KG, userUnit)
                    ?.let { String.format("%.1f", it) }.orEmpty()
            )
            sb.append(userUnit.toString().lowercase())
            sb.append(" | ")
        }
        sb.append(set.reps ?: " ")
        if (set.side == SetSide.LEFT) {
            sb.append("/")
            sb.append(set.linkedRightSet?.reps ?: "_")
        }
        sb.append(" - ")
        previousCharge = set.charge
        if (sb.length >= 40) {
            sb.append("...")
            stop = true
        }
    }
    if (!stop && previousCharge != null) {
        sb.append(
            convertWeight(previousCharge, MesureUnit.KG, userUnit)
                ?.let { String.format("%.1f", it) }.orEmpty()
        )
        sb.append(userUnit.toString().lowercase())
    }
    return sb.toString()
}
