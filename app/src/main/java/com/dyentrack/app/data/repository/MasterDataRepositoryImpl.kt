package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.datasource.LocalDataSource
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.repository.MasterDataRepository
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MasterDataRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val localDataSource: LocalDataSource
) : MasterDataRepository {
    private var cachedMasterData: MasterDataStatic? = null
    override suspend fun getMasterDataStatic(): MasterDataStatic {
        //cache
        cachedMasterData?.let { return it }
        //local
        localDataSource.getMasterData()?.let {
            it.dateEnregistement?.let { dateStr ->
                val savedDate = Instant.parse(dateStr)
                val now = Instant.now()
                val diffDays = Duration.between(savedDate, now).toDays()

                if (diffDays < 2) {
                    cachedMasterData = it
                    return it
                }
            }
        }
        //api
        val response = api.getMasterDataStatic()
        if (response.status == 200) {
            val masterData = response.data.copy(dateEnregistement = response.timestamp.toString())
            if (response.data.officialExercise.isNotEmpty() && response.data.muscles.isNotEmpty() && response.data.musclesGroup.isNotEmpty()) {
                localDataSource.saveMasterData(masterData)
            }
            cachedMasterData = masterData
            return masterData
        } else {
            throw Exception("Erreur: ${response.message}")
        }
    }


}
