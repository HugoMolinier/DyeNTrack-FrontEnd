package com.dyentrack.app.data.local.datasource

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dyentrack.app.data.local.entities.SessionLocal
import com.dyentrack.app.data.local.entities.UserLocal
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SecureSessionDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val gson = Gson()

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "user_session_secure",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_SESSION = "session"
    }

    /* -------- USER -------- */

    fun saveUser(user: UserLocal) {
        Log.d("saveUser","save")
        prefs.edit { putString(KEY_USER, gson.toJson(user)) }
    }

    fun getUser(): UserLocal? {
        return prefs.getString(KEY_USER, null)?.let { gson.fromJson(it, UserLocal::class.java) }
    }

    fun clearUser() {
        prefs.edit { remove(KEY_USER) }
    }

    /* -------- SESSION -------- */

    fun saveSession(session: SessionLocal) {
        Log.d("saveSession","${session}")
        prefs.edit { putString(KEY_SESSION, gson.toJson(session)) }
    }

    fun getSession(): SessionLocal? {
        return prefs.getString(KEY_SESSION, null)?.let { gson.fromJson(it, SessionLocal::class.java) }

    }
    fun getTokenAuthInterceptor(): String? {
        val session = prefs.getString(KEY_SESSION, null)?.let {
            gson.fromJson(it, SessionLocal::class.java)
        }

        // Vérifie l'expiration
        if (session != null) {
            val currentTime = System.currentTimeMillis()
            if (session.expirationTime != null && currentTime > session.expirationTime) {
                clearSession() // supprime la session expirée
                return null
            }
            return session.token
        }

        return null
    }
    fun clearSession() {
        Log.d("clear","cleaejkiozrjz")
        prefs.edit { remove(KEY_SESSION) }
    }

    fun clearAll() {
        Log.d("clearall","cleaejkiozrjz")
        prefs.edit { clear() }
    }
}
