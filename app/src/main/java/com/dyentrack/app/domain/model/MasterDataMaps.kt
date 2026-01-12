package com.dyentrack.app.domain.model

import com.dyentrack.app.domain.model.exercise.Exercise

data class MasterDataMaps(
    val exerciseMap: Map<Long, Exercise> = emptyMap(),
    val muscleMap: Map<Long, ReturnIdAndName> = emptyMap(),

    //Ne contient que id + name
    val musclesGroupMap: Map<Long, String> = emptyMap(),
    val lateraliteMap: Map<Long, String> = emptyMap(),
    val equipmentMap: Map<Long, String> = emptyMap()
) {
    companion object {
        fun empty(language: LANGUAGE) = MasterDataMaps()
    }
}