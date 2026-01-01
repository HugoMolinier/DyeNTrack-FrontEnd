package com.dyentrack.app.domain.model

data class ReturnIdAndName (
    val id: Long,
    override val nameFR: String,
    override val nameEN: String,
    val idMuscularGroup: Long?
): HasLocalizedName