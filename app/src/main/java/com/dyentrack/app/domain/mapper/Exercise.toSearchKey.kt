package com.dyentrack.app.domain.mapper

import com.dyentrack.app.domain.model.exercise.Exercise

fun Exercise.toSearchKey(): String {
    return listOfNotNull(nameFR, nameEN)
        .joinToString(" ")
        .normalize()
}
