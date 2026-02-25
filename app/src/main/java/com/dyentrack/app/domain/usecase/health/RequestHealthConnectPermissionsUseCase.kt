package com.dyentrack.app.domain.usecase.health

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RequestHealthConnectPermissionsUseCase @Inject constructor() {

    fun getRequiredPermissions(): Set<String> {
        return setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
        )
    }

    suspend fun arePermissionsGranted(
        healthConnectClient: HealthConnectClient
    ): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(getRequiredPermissions())
    }
}