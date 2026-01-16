package com.dyentrack.app.core.locale

import android.content.Context
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.UserPreference
import com.google.gson.Gson
import java.util.Locale

object LocaleManager {

    fun updateContext(context: Context): Context {
        val prefs = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
        val json = prefs.getString("user_preference", null)

        val language = json
            ?.let { Gson().fromJson(it, UserPreference::class.java).language }
            ?: LANGUAGE.FR

        val locale = when (language) {
            LANGUAGE.EN -> Locale.ENGLISH
            LANGUAGE.FR -> Locale.FRENCH
        }
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }



    fun getCurrentLanguage(): LANGUAGE =
        when(Locale.getDefault().language.lowercase()) {
            "fr" -> LANGUAGE.FR
            "en" -> LANGUAGE.EN
            else -> LANGUAGE.FR
        }
}
