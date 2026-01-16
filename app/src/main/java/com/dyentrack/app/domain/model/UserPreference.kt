package com.dyentrack.app.domain.model

import com.dyentrack.app.domain.model.enums.SyncPerformanceMode

data class UserPreference(
    val autoSync: Boolean? = true,//pas api
    val syncPerformanceMode: SyncPerformanceMode? = SyncPerformanceMode.ALL,
    val objCalorie: Int? = null,
    val objStep: Int? = null,
    val objHourSleep: Double? = null,

    val mesureUnit: MesureUnit? = MesureUnit.KG,
    val language: LANGUAGE? = LANGUAGE.FR
)

enum class MesureUnit { KG, LBS }
enum class LANGUAGE { FR, EN }
