package com.dyentrack.app.data.local.datasource

import android.content.Context
import androidx.core.content.edit
import com.dyentrack.app.domain.model.MasterDataStatic
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
//(langue, unités, master data, config)
class FileLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalDataSource {
    private val prefs = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)

    override fun getMasterData(): MasterDataStatic? {
        val json = prefs.getString("master_data_static", null)
        return json?.let { Gson().fromJson(it, MasterDataStatic::class.java) }
    }

    override fun saveMasterData(masterDataStatic: MasterDataStatic) {
        val json = Gson().toJson(masterDataStatic)
        prefs.edit { putString("master_data_static", json) }
    }


}