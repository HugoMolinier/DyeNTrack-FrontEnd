package com.dyentrack.app.data.network

import android.util.Log
import com.dyentrack.app.BuildConfig
import com.dyentrack.app.data.local.datasource.SecureSessionDataSource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(
    private val secureSessionDataSource: SecureSessionDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = secureSessionDataSource.getTokenAuthInterceptor()
        val request = chain.request().newBuilder().addHeader("X-API-KEY", BuildConfig.API_KEY)
            .addHeader("Accept", "application/json").apply {
                if (!token.isNullOrBlank()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }.build()

        //Log.d("AuthInterceptor", "request: $request")


        return chain.proceed(request)
    }
}
