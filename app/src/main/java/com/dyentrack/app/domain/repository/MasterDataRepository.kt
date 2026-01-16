package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.MasterDataStatic
import kotlinx.coroutines.flow.Flow

interface MasterDataRepository {
    val masterDataFlow: Flow<MasterDataStatic?>

    suspend fun refreshMasterDataIfNeeded()
}