package com.dyentrack.app.domain.model

data class UserPreference(
    val autoSync:Boolean?=true,//pas api
    val objCalorie:Int?=null,
    val objStep: Int?=null,
    val objHourSleep: Double?=null,
)
