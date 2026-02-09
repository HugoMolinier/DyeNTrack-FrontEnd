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

        val language  = json
            ?.let { Gson().fromJson(it, UserPreference::class.java).language }
            ?:getSystemLanguage(context)

        val locale = when (language) {
            LANGUAGE.FR -> Locale.FRENCH
            else  -> Locale.ENGLISH
        }
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    private fun getSystemLanguage(context: Context): LANGUAGE {
        val locale = context.resources.configuration.locales[0]
        return when (locale.language.lowercase()) {
            "fr" -> LANGUAGE.FR
            else -> LANGUAGE.EN
        }
    }
    fun getCurrentLanguage(): LANGUAGE =
        when(Locale.getDefault().language.lowercase()) {
            "fr" -> LANGUAGE.FR
            else -> LANGUAGE.EN
        }
}
