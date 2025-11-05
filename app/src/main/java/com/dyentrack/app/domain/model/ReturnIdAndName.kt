package com.dyentrack.app.domain.model

data class ReturnIdAndName (
    val id: Long,
    val nameFR: String,
    val nameEN: String,
    val idMuscularGroup: Long?
)