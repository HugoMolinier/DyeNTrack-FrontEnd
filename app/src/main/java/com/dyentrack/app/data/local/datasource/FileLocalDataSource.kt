package com.dyentrack.app.data.local.datasource

import android.content.Context
import androidx.core.content.edit
import com.dyentrack.app.core.utils.GsonProvider
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.UserPreference
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
        return json?.let { GsonProvider.gson.fromJson(it, MasterDataStatic::class.java) }
    }

    override fun saveMasterData(masterDataStatic: MasterDataStatic) {
        val json = GsonProvider.gson.toJson(masterDataStatic)
        prefs.edit { putString("master_data_static", json) }
    }

    override fun getUserPreference(): UserPreference? {
        val json = prefs.getString("user_preference", null)
        return json?.let { GsonProvider.gson.fromJson(it, UserPreference::class.java) }
    }

    override fun saveUserPreference(userPreference: UserPreference) {
        val json = GsonProvider.gson.toJson(userPreference)
        prefs.edit { putString("user_preference", json) }
    }


}