package com.dyentrack.app.data.local.datasource

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dyentrack.app.core.utils.GsonProvider
import com.dyentrack.app.data.local.entities.SessionLocal
import com.dyentrack.app.data.local.entities.UserLocal
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureSessionDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences by lazy {
        createEncryptedPrefs()
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        return try {
            val masterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

            EncryptedSharedPreferences.create(
                context,
                "user_session_secure", // nom constant
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w("SecurePrefs", "Cannot decrypt keyset, recreating prefs", e)
            context.deleteSharedPreferences("user_session_secure")
            createEncryptedPrefs()
        }
    }

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_SESSION = "session"
    }

    /* -------- USER -------- */

    fun saveUser(user: UserLocal) {
        prefs.edit { putString(KEY_USER, GsonProvider.gson.toJson(user)) }
    }

    fun updateUserInfo(
        pseudo: String, birthdate: String?, height: Int?, sexe: Boolean?
    ) {
        val currentUser = getUser() ?: return

        val updatedUser = currentUser.copy(
            pseudo = pseudo, birthdate = birthdate, height = height, sexe = sexe
        )

        saveUser(updatedUser)
    }


    fun getUser(): UserLocal? {
        return prefs.getString(KEY_USER, null)?.let { GsonProvider.gson.fromJson(it, UserLocal::class.java) }
    }

    /* -------- SESSION -------- */

    fun saveSession(session: SessionLocal) {
        prefs.edit { putString(KEY_SESSION, GsonProvider.gson.toJson(session)) }
    }

    fun getSession(): SessionLocal? {
        return prefs.getString(KEY_SESSION, null)
            ?.let { GsonProvider.gson.fromJson(it, SessionLocal::class.java) }

    }

    fun getTokenAuthInterceptor(): String? {
        val session = prefs.getString(KEY_SESSION, null)?.let {
            GsonProvider.gson.fromJson(it, SessionLocal::class.java)
        }

        // Vérifie l'expiration
        if (session != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime > session.expirationTime) {
                clearSession() // supprime la session expirée
                return null
            }
            return session.token
        }

        return null
    }

    fun clearSession() {
        prefs.edit { remove(KEY_SESSION) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
