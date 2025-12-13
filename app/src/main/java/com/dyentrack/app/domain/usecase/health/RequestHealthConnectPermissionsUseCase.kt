package com.dyentrack.app.domain.usecase.health

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.permission.HealthPermission
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RequestHealthConnectPermissionsUseCase @Inject constructor() {

    fun getRequiredPermissions(): Set<String> {
        return setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(BodyFatRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(HeightRecord::class)
        )
    }

    suspend fun arePermissionsGranted(
        healthConnectClient: HealthConnectClient
    ): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(getRequiredPermissions())
    }
}