package com.dyentrack.app.domain.model

import androidx.health.connect.client.HealthConnectClient

sealed class HealthConnectStatus {
    object Loading : HealthConnectStatus()
    object NotAvailable : HealthConnectStatus()
    data class NeedsUpdate(val packageName: String) : HealthConnectStatus()
    data class Available(val client: HealthConnectClient) : HealthConnectStatus()
}
