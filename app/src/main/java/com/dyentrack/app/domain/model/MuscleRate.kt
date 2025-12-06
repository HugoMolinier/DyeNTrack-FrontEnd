package com.dyentrack.app.domain.model

data class MuscleRate(
    val numberExercise: Int,
    val numberSet: Int,
    val rateSetSomme: RateSetSomme?=null
)

data class RateSetSomme(
    val numberSetFail: Int?=null,
    val sommePond: Double?=null
)