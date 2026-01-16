package com.dyentrack.app.presentation.screens.seance.dataSection.component

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.health.connect.client.PermissionController
import com.dyentrack.app.presentation.globalViewModel.HealthConnectViewModel
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel

@Composable
fun HealthConnectPermissionsScreen(
    healthConnectPermissionsNeeded: Set<String>,
    viewModel: HealthConnectViewModel
) {
    val launcher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->

        viewModel.onPermissionsResult(
            grantedPermissions.containsAll(healthConnectPermissionsNeeded)
        )
    }

    LaunchedEffect(healthConnectPermissionsNeeded) {
        launcher.launch(healthConnectPermissionsNeeded)
    }
}


