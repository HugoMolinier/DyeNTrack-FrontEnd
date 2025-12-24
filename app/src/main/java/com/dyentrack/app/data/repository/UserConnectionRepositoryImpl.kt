package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.datasource.SecureSessionDataSource
import com.dyentrack.app.data.local.datasource.toDomain
import com.dyentrack.app.data.local.datasource.toLocal
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.data.network.dto.IdentificationRequest
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.ResponseAuth
import com.dyentrack.app.domain.model.User
import com.dyentrack.app.domain.repository.UserConnectionRepository
import retrofit2.HttpException
import javax.inject.Inject

class UserConnectionRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val secureSessionDataSource: SecureSessionDataSource
) : UserConnectionRepository {

    override suspend fun loginUser(email: String, password: String): ResponseAuth {
            if (!isValideMail(email)) {
                return ResponseAuth(
                    userDTO = null,
                    token = null,
                    status = AuthStatus.NOT_CONNECTED,
                    error = AuthError.NOT_EMAILFORMAT
                )
            }
            if (!isValidPassword(password)) {
                return ResponseAuth(
                    userDTO = null,
                    token = null,
                    status = AuthStatus.NOT_CONNECTED,
                    error = AuthError.NOT_PASSWORD
                )
            }
        return try {
            val response = api.loginUser(IdentificationRequest(email, password))
            val data = response.data
            val user = data.userDTO
            val token = data.token
            if (user == null || token == null) {
                return ResponseAuth(
                    null,
                    null,
                    AuthStatus.NOT_CONNECTED,
                    AuthError.ERROR_API
                )
            }
            secureSessionDataSource.saveUser(user.toLocal())
            secureSessionDataSource.saveSession(token.toLocal())

            return data.copy(status = AuthStatus.CONNECTED)
        } catch (e: HttpException) {
            val error = if (e.code() == 401) {
                AuthError.UNAUTHORIZED
            } else {
                AuthError.ERROR_API
            }

            ResponseAuth(
                null,
                null,
                AuthStatus.NOT_CONNECTED,
                error
            )

        } catch (e: Exception) {
            ResponseAuth(
                null,
                null,
                AuthStatus.NOT_CONNECTED,
                AuthError.ERROR_API
            )
        }
    }

    override suspend fun registerUser(email: String, password: String): ResponseAuth {
        return try {
            val data = api.registerUser(
                IdentificationRequest(email, password)
            ).data

            val user = data.userDTO
            val token = data.token

            if (user == null || token == null) {
                return ResponseAuth(
                    null,
                    null,
                    AuthStatus.NOT_CONNECTED,
                    AuthError.ERROR_API
                )
            }

            secureSessionDataSource.saveUser(user.toLocal())
            secureSessionDataSource.saveSession(token.toLocal())

            ResponseAuth(
                userDTO = user,
                token = token,
                status = AuthStatus.CONNECTED,
                null
            )

        } catch (e: Exception) {
            ResponseAuth(
                null,
                null,
                AuthStatus.NOT_CONNECTED,
                AuthError.ERROR_API
            )
        }
    }

    override fun isValideToken(): AuthStatus {
        val session = secureSessionDataSource.getSession()
            ?: return AuthStatus.NOT_CONNECTED

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

    override fun logout():ResponseAuth {
        secureSessionDataSource.clearAll()
        return             ResponseAuth(
            null,
            null,
            AuthStatus.NOT_CONNECTED,
            null
        )
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

}