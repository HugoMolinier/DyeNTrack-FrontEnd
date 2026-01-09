package com.dyentrack.app.domain.usecase.masterdata

import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.repository.MasterDataRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Sert a Eviter un maximum de masterData.find comme master Data est presque fixe
 */
@Singleton
class BuildMasterDataMapsUseCase @Inject constructor(
    private val repository: MasterDataRepository
) {

    private val cache = mutableMapOf<LANGUAGE, MasterDataMaps>()

    suspend fun execute(language: LANGUAGE): MasterDataMaps {
        cache[language]?.let { return it }

        val data = repository.masterDataFlow
            .filterNotNull()
            .first()
        return MasterDataMaps(
            exerciseMap = data.officialExercise.associateBy { it.idExercise },
            musclesGroupMap = data.musclesGroup.associate { it.id to it.getName(language) },
            muscleMap = data.muscles.associateBy { it.id },
            lateraliteMap = data.lateralites.associate { it.id to it.getName(language) },
            equipmentMap = data.equipment.associate { it.id to it.getName(language) }
        ).also {
            cache[language] = it
        }
    }
}
