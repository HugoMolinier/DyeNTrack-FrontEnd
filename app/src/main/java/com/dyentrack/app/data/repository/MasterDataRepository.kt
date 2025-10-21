package com.dyentrack.app.data.repository

import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MasterDataRepository @Inject constructor(private val api: ApiInterface) {
    suspend fun getMasterDataStatic(): MasterDataStatic {
        val response = api.getMasterDataStatic()
        if (response.status == 200) {
            return response.data
        } else {
            throw Exception("Erreur: ${response.message}")
        }
    }
}
