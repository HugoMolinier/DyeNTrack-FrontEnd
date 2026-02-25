package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.datasource.SecureSessionDataSource
import com.dyentrack.app.data.local.datasource.toDomain
import com.dyentrack.app.data.local.datasource.toLocal
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.data.network.dto.IdentificationRequest
import com.dyentrack.app.data.network.dto.UserUpdate
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.AuthState
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.model.User
import com.dyentrack.app.domain.repository.UserConnectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserConnectionRepositoryImpl @Inject constructor(
    private val api: ApiInterface, private val secureSessionDataSource: SecureSessionDataSource
) : UserConnectionRepository {

    private val _authState = MutableStateFlow(
        AuthState(
            isConnected = isValideToken() == AuthStatus.CONNECTED, user = getUser()
        )
    )
    override val authState: StateFlow<AuthState> = _authState

    override suspend fun updateUser(
        pseudo: String, height: Int?, sexe: Boolean?, birthDate: String?
    ): RequestState {
        if (!isValidPseudo(pseudo)) {
            return RequestState.Error(AuthError.NOT_PSEUDOFORMAT)
        }
        if (height != null && !isValidHeight(height)) {
            return RequestState.Error(AuthError.NOT_HEIGHTFORMAT)
        }
        val date = birthDate?.let {
            runCatching {
                LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE).toString()
            }.getOrNull()
        }


        return try {
            val response = api.updateUser(UserUpdate(pseudo, date, height, sexe))

            val user = response.data

            secureSessionDataSource.updateUserInfo(
                user.pseudo, user.birthdate, user.height, user.sexeMale
            )

            updateAuthState()
            return RequestState.Success
        } catch (e: HttpException) {
            RequestState.Error(
                if (e.code() == 401) AuthError.UNAUTHORIZED
                else AuthError.ERROR_API
            )
        } catch (_: Exception) {
            RequestState.Error(AuthError.ERROR_API)
        }
    }

    override suspend fun loginUser(email: String, password: String): RequestState {
        if (!isValideMail(email)) {
            return RequestState.Error(AuthError.NOT_EMAILFORMAT)
        }
        if (!isValidPassword(password)) {
            return RequestState.Error(AuthError.NOT_PASSWORD)
        }
        return try {
            val response = api.loginUser(IdentificationRequest(email, password))
            val data = response.data
            val user = data.userDTO
            val token = data.token
            if (user == null || token == null) {
                return RequestState.Error(AuthError.ERROR_API)
            }
            secureSessionDataSource.saveUser(user.toLocal())
            secureSessionDataSource.saveSession(token.toLocal())

            updateAuthState()
            return RequestState.Success
        } catch (e: HttpException) {
            RequestState.Error(
                if (e.code() == 401) AuthError.UNAUTHORIZED else AuthError.ERROR_API
            )
        } catch (e: Exception) {
            //Log.d("ErrorLogin","$e")
            RequestState.Error(AuthError.ERROR_API)
        }
    }

    override suspend fun registerUser(
        email: String, password: String, pseudo: String
    ): RequestState {
        if (!isValideMail(email) || email.length > 100) {
            return RequestState.Error(AuthError.NOT_EMAILFORMAT)
        }
        if (!isValidPseudo(pseudo)) {
            return RequestState.Error(AuthError.NOT_PSEUDOFORMAT)
        }
        if (!isValidPassword(password) || password.length > 60) {
            return RequestState.Error(AuthError.NOT_PASSWORD)
        }
        return try {
            val data = api.registerUser(
                IdentificationRequest(email, password, pseudo)
            ).data

            val user = data.userDTO ?: return RequestState.Error(AuthError.ERROR_API)
            val token = data.token ?: return RequestState.Error(AuthError.ERROR_API)


            secureSessionDataSource.saveUser(user.toLocal())
            secureSessionDataSource.saveSession(token.toLocal())

            updateAuthState()

            RequestState.Success

        } catch (e: HttpException) {
            RequestState.Error(
                if (e.code() == 401) AuthError.UNAUTHORIZED
                else AuthError.ERROR_API
            )
        } catch (_: Exception) {
            RequestState.Error(AuthError.ERROR_API)
        }
    }

    fun isValideToken(): AuthStatus {
        val session = secureSessionDataSource.getSession() ?: return AuthStatus.NOT_CONNECTED

        return if (System.currentTimeMillis() > session.expirationTime) {
            secureSessionDataSource.clearSession()
            AuthStatus.NOT_CONNECTED
        } else {
            AuthStatus.CONNECTED
        }
    }

    override fun getToken(): String {
        return if (isValideToken() == AuthStatus.CONNECTED) {
            secureSessionDataSource.getSession()?.token.orEmpty()
        } else {
            ""
        }
    }

    override fun getUser(): User? {
        return secureSessionDataSource.getUser()?.toDomain()
    }

    override fun logout() {
        secureSessionDataSource.clearAll()
        updateAuthState()
    }

    override suspend fun deleteAll(): RequestState {
        return try {

                 api.deleteUser()
                logout()
                RequestState.Success
        } catch (e: Exception) {
            //Log.d("dsds","$e")
            RequestState.Error(AuthError.ERROR_API)
        }
    }

    /**
     * 2 caractères avant @, au moins 1 caractère entre @ et ., au moins 1 caractère après .
     */
    private fun isValideMail(email: String): Boolean {
        val regex = Regex("^[^@]{2,}@[^\n@.]+\\.[^\n@.]+$")
        return regex.matches(email)
    }

    /**
     * au moins 8 caractères , 1 caractere et 1 chiffre
     */
    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
        return regex.matches(password)
    }

    private fun isValidPseudo(pseudo: String): Boolean {
        return pseudo.length in 3..20
    }

    private fun isValidHeight(height: Int): Boolean {
        return height in 50..300
    }


    private fun updateAuthState() {
        _authState.value = AuthState(
            isConnected = isValideToken() == AuthStatus.CONNECTED, user = getUser()
        )
    }
}