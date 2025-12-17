package com.dyentrack.app.domain.model

data class MuscleRate(
    val numberExercise: Int=0,
    val numberSet: Int=0,
    val rateSetSomme: RateSetSomme?=null
)

data class RateSetSomme(
    val numberSetFail: Int?=null,
    val sommePond: Double?=null,
    val numberRep:Double?=null
)