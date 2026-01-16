package com.dyentrack.app.domain.model.preset

data class ExerciceInPreset(
    val idExercise:Long,
    val nbSet: Int=1,
    val rangeRepInf:Int?=null,
    val rangeRepSup:Int?=null,
    val idLateralite:Long=1L,
    val idEquipment:Long=1L
)