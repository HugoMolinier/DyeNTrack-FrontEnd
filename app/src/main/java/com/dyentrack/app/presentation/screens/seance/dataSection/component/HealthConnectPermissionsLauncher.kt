package com.dyentrack.app.presentation.screens.seance.dataSection.component

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.health.connect.client.PermissionController
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel

@Composable
fun HealthConnectPermissionsScreen(
    healthConnectPermissionsNeeded: Set<String>,
    viewModel: SeanceViewModel
) {
    val launcher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        // Résultat des permissions
        if (grantedPermissions.containsAll(healthConnectPermissionsNeeded)) {
            Log.d("HealthConnect", "Permissions accordées")
        } else {
            Log.d("HealthConnect", "Permissions refusées ou incomplètes")
        }
        // Relancer la synchro pour mettre à jour l'état
        viewModel.syncHealthData()
    }

    LaunchedEffect(healthConnectPermissionsNeeded) {
        launcher.launch(healthConnectPermissionsNeeded)
    }
}


