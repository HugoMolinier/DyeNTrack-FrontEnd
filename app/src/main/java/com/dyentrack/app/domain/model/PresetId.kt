package com.dyentrack.app.domain.model

data class PresetId(
    val apiId: Long? = null, val localId: Long? = null
) {
    fun resolved(): Long? = apiId ?: localId
}
