package com.dyentrack.app.data.local.datasource

import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.UserPreference

interface LocalDataSource {
    fun getMasterData(): MasterDataStatic?

    fun saveMasterData(masterDataStatic: MasterDataStatic)

    fun getUserPreference(): UserPreference?

    fun saveUserPreference(userPreference:UserPreference)
}