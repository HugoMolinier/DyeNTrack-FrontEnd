package com.dyentrack.app.data.local.datasource

import com.dyentrack.app.domain.model.MasterDataStatic

interface LocalDataSource {
    fun getMasterData(): MasterDataStatic?

    fun saveMasterData(masterDataStatic: MasterDataStatic)
}