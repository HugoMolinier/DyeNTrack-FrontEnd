package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.MasterDataStatic

interface MasterDataRepository {
    suspend fun getMasterDataStatic(): MasterDataStatic
}