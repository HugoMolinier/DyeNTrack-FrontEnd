package com.dyentrack.app.domain.model

data class UserPreference(
    val autoSync:Boolean?=true,//pas api
    val objCalorie:Int?=null,
    val objStep: Int?=null,
    val objHourSleep: Double?=null,

    val mesureUnit:MesureUnit?=MesureUnit.KG,
    val language:LANGUAGE?=LANGUAGE.FR
)

enum class MesureUnit { KG, LBS }
enum class LANGUAGE{ FR,EN}
