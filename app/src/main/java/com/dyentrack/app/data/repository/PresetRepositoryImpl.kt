package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.database.PresetDao
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.repository.PresetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresetRepositoryImpl @Inject constructor(
    private val presetDao: PresetDao, private val api: ApiInterface,
    private val userConnectionRepositoryImpl: UserConnectionRepositoryImpl
) : PresetRepository {

    override val presetFlow: Flow<List<Preset>> = presetDao.getAllActivePresetsFlow()


    // récupérer tous les presets
    override suspend fun syncBetweenLocalApi() {
        try {
            if (userConnectionRepositoryImpl.isValideToken() == AuthStatus.CONNECTED) {
                val apiPresets = try {
                    val response = api.getAllPreset()
                    if (response.status == 200) response.data else emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
                val localPresets = presetFlow.first()

                // Merge local - API
                mergePreset(localPresets, apiPresets)

                //sync les delete
                presetDao.getDeletedPresets().forEach {
                    deleteHardPreset(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun mergePreset(
        localp: List<Preset>,
        apip: List<Preset>
    ): List<Preset> {

        val result = mutableListOf<Preset>()
        val apiMap = apip.associateBy { it.idPreset }

        for (localPreset in localp) {
            val apiPreset = localPreset.idPreset?.let { apiMap[it] }

            if (apiPreset != null) {
                // Comparer lastUpdate
                val localTime = Instant.parse(localPreset.lastUpdate)
                val apiTime = Instant.parse(apiPreset.lastUpdate)

                when {
                    apiTime > localTime -> {
                        // API est plus récent → utiliser API
                        result.add(apiPreset)
                        // Update local avec données API
                        presetDao.saveOrUpdatePreset(
                            apiPreset.copy(
                                idLocal = localPreset.idLocal,
                                idPreset = apiPreset.idPreset
                            ),
                            dateSave = apiPreset.lastUpdate
                        )
                    }

                    localTime > apiTime -> {
                        // Local est plus récent → garder local
                        result.add(localPreset)
                        // Synchroniser local → API
                        if (userConnectionRepositoryImpl.isValideToken() == AuthStatus.CONNECTED) {
                            val response = api.savePreset(localPreset)
                            if (response.status==200) {
                                val presetApiObtain = response.data
                                presetDao.saveOrUpdatePreset(
                                    presetApiObtain.copy(
                                        idLocal = localPreset.idLocal,
                                        idPreset = presetApiObtain.idPreset
                                    ),
                                    dateSave = presetApiObtain.lastUpdate
                                )
                            }
                        }
                    }

                    else -> {
                        // Même date → garder local
                        result.add(localPreset)
                    }
                }
            } else {
                // Aucun preset correspondant côté API → garder local + push API
                result.add(localPreset)
                if (userConnectionRepositoryImpl.isValideToken() == AuthStatus.CONNECTED) {
                    val response = api.savePreset(localPreset)
                    if (response.status==200) {
                        val presetApiObtain = response.data
                        presetDao.saveOrUpdatePreset(
                            presetApiObtain.copy(
                                idLocal = localPreset.idLocal,
                                idPreset = presetApiObtain.idPreset
                            ),
                            dateSave = presetApiObtain.lastUpdate
                        )
                    }
                }
            }
        }

        // Ajouter les presets API absents localement
        val localIds = localp.mapNotNull { it.idPreset }.toSet()
        for (apiOnly in apip) {
            if (apiOnly.idPreset != null && apiOnly.idPreset !in localIds) {
                if(presetDao.getPresetByIdApi(apiOnly.idPreset)?.isDelete!=true) {
                    result.add(apiOnly)
                    presetDao.saveOrUpdatePreset(apiOnly)
                }
            }
        }

        return result
    }

    override suspend fun saveLocalOnly(preset: Preset): Preset? {
        return presetDao.saveOrUpdatePreset(preset)
    }

    // sauvegarder ou mettre à jour un preset
    override suspend fun save(preset: Preset): Preset? {
        try {
            val localPreset = (presetDao.saveOrUpdatePreset(preset))
            var resultPreset = localPreset
            if (userConnectionRepositoryImpl.isValideToken() == AuthStatus.CONNECTED) {
                val apiPreset = api.savePreset(preset)
                if (apiPreset.status == 200) {
                    Log.d("apiResponsePreset","${apiPreset}")
                    val updatedLocal = localPreset?.copy(
                        idPreset = apiPreset.data.idPreset,
                        lastUpdate = apiPreset.data.lastUpdate
                    )
                    presetDao.updatePreset(updatedLocal!!)
                    resultPreset = updatedLocal
                }

            }
            return resultPreset

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override suspend fun delete(preset: Preset) {
        try {
            presetDao.updatePreset(preset.copy(isDelete = true))

            deleteHardPreset(preset)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteHardPreset(preset: Preset) {
        if (preset.idPreset != null) {
            if (userConnectionRepositoryImpl.isValideToken() == AuthStatus.CONNECTED) {
                val response = api.deletePreset(preset.idPreset)
                Log.d("apiDeleterespons","${response}")
                if (response.status == 200) {
                    presetDao.hardDeleteByApiId(preset.idPreset)
                }

            }
        } else {
            //jamais sync côté serveur → hard delete local directement
            preset.idLocal?.let { localId ->
                presetDao.hardDeleteByLocalId(localId)
            }
        }


    }

}

