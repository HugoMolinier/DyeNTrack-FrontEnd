package com.dyentrack.app.domain.model

data class MuscleRate(
    val numberExercise: Int,
    val numberSet: Int,
    val numberSetFail: Int?=null,
    val sommePond: Double?=null
)
