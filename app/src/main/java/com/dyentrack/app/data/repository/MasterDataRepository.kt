package com.dyentrack.app.data.repository

import com.dyentrack.app.data.local.LocalDataSource
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MasterDataRepository @Inject constructor(private val api: ApiInterface,
    private val localDataSource: LocalDataSource) {

    suspend fun getMasterDataStatic(): MasterDataStatic {
        localDataSource.getMasterData()?.let { return it }

        val response = api.getMasterDataStatic()
        if (response.status == 200) {
            if (response.data.officialExercise.isNotEmpty() && response.data.muscles.isNotEmpty() && response.data.musclesGroup.isNotEmpty()) {
                localDataSource.saveMasterData(response.data)
            }
            return response.data
        } else {
            throw Exception("Erreur: ${response.message}")
        }
    }


}
