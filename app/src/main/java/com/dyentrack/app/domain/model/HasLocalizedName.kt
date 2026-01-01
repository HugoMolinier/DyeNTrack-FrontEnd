package com.dyentrack.app.domain.model

interface HasLocalizedName {
    val nameFR: String
    val nameEN: String
}

fun HasLocalizedName.getName(lang: LANGUAGE): String =
    when (lang) {
        LANGUAGE.FR -> nameFR.ifBlank { "NomFR" }
        LANGUAGE.EN -> nameEN.ifBlank { nameFR }
    }
