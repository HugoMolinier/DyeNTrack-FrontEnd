package com.dyentrack.app.presentation.utils

import com.dyentrack.app.domain.model.preset.ExerciceInPreset

fun ExerciceInPreset.formatSubtitle(
    setPlural: (Int) -> String,
    repPlural: (Int) -> String
): String {
    val repText = when {
        rangeRepInf != null && rangeRepSup != null ->
            "$rangeRepInf - $rangeRepSup"
        rangeRepInf != null ->
            "$rangeRepInf"
        rangeRepSup != null ->
            "$rangeRepSup"
        else -> null
    }

    val repCount = rangeRepSup ?: rangeRepInf ?: 0

    return buildString {
        append("$nbSet ")
        append(setPlural(nbSet))

        if (repText != null) {
            append(" x ")
            append(repText)
            append(" ")
            append(repPlural(repCount))
        }
    }
}
