package com.dyentrack.app.data.local

import android.content.Context
import com.dyentrack.app.domain.model.MasterDataStatic
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
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