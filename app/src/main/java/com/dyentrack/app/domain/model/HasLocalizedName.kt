package com.dyentrack.app.domain.model

interface HasLocalizedName {
    val nameFR: String
    val nameEN: String
}

interface HasLocalizedDescription {
    val descriptionFR: String?
    val descriptionEN: String?
}


fun HasLocalizedName.getName(lang: LANGUAGE): String =
    when (lang) {
        LANGUAGE.FR -> nameFR.ifBlank { "NomFR" }
        LANGUAGE.EN -> nameEN.ifBlank { nameFR }
    }

fun HasLocalizedDescription.getDescription(lang: LANGUAGE): String =
    when (lang) {
        LANGUAGE.FR -> descriptionFR?.ifBlank { "" }?:""
        LANGUAGE.EN -> descriptionEN?.ifBlank { "" }?:""
    }

