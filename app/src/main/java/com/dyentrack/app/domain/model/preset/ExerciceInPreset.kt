package com.dyentrack.app.domain.model.preset

data class ExerciceInPreset(
    val idExercise:Long,
    val parameter: String,
    val rangeRepInf:Int,
    val rangeRepSup:Int,
    val idLateralite:Long,
    val idEquipment:Long
)