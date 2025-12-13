package com.dyentrack.app.domain.usecase.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.dyentrack.app.domain.model.HealthConnectStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckHealthConnectUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(): HealthConnectStatus {
        val provider = "com.google.android.apps.healthdata"
        val status = HealthConnectClient.Companion.getSdkStatus(context)

        return when (status) {
            HealthConnectClient.Companion.SDK_UNAVAILABLE ->
                HealthConnectStatus.NotAvailable

            HealthConnectClient.Companion.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
                HealthConnectStatus.NeedsUpdate(provider)

            else -> {
                val client = HealthConnectClient.Companion.getOrCreate(context)
                HealthConnectStatus.Available(client)
            }
        }
    }
}