package com.dyentrack.app.domain.usecase.masterdata

import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.repository.MasterDataRepository
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Sert a Eviter un maximum de masterData.find comme master Data est presque fixe
 */
@Singleton
class BuildMasterDataMapsUseCase @Inject constructor(
    private val repository: MasterDataRepository
) {

    private var cachedMaps: MasterDataMaps? = null
    private var cachedLanguage: LANGUAGE? = null

    suspend fun execute(language: LANGUAGE): MasterDataMaps {
        if (cachedMaps != null && cachedLanguage == language) {
            return cachedMaps!!
        }

        val data = repository.getMasterDataStatic()

        val maps = MasterDataMaps(
            exerciseMap = data.officialExercise.associateBy { it.idExercise },
            musclesGroupMap = data.musclesGroup.associate { it.id to it.getName(language) },
            muscleMap =  data.muscles.associateBy { it.id },
            lateraliteMap = data.lateralites.associate { it.id to it.getName(language) },
            equipmentMap = data.equipment.associate { it.id to it.getName(language) }
        )
        cachedMaps = maps
        return maps
    }
}
