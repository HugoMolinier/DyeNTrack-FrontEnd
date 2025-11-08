package com.dyentrack.app.data.repository

import com.dyentrack.app.data.local.datasource.LocalDataSource
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.repository.MasterDataRepository
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
            cachedMasterData = it
            return it
        }
        //api
        val response = api.getMasterDataStatic()
        if (response.status == 200) {
            if (response.data.officialExercise.isNotEmpty() && response.data.muscles.isNotEmpty() && response.data.musclesGroup.isNotEmpty()) {
                localDataSource.saveMasterData(response.data)
            }
            cachedMasterData = response.data
            return response.data
        } else {
            throw Exception("Erreur: ${response.message}")
        }
    }


}
