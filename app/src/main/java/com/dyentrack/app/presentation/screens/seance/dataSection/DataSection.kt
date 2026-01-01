package com.dyentrack.app.presentation.screens.seance.dataSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.presentation.globalViewModel.HealthConnectViewModel
import com.dyentrack.app.presentation.screens.seance.dataSection.component.HealthConnectPermissionsScreen
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceUiState
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun DataSection(
    viewModel: SeanceViewModel = viewModel(),
    lockInput: Boolean = false,
    day: String,
    state: SeanceUiState,
    healthViewModel: HealthConnectViewModel = viewModel(),
    isAutoSync: Boolean,
    userUnit: MesureUnit
) {

    val autorisationNeed by healthViewModel.autorisationNeed.collectAsState()

    Column(
        modifier = Modifier.padding(10.dp, 4.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        NutritionBox(viewModel, state, lockInput, day, isAutoSync)

        PhysioBox(viewModel, state, lockInput, day, isAutoSync, userUnit  )

        if (autorisationNeed?.isNotEmpty() == true) {
            HealthConnectPermissionsScreen(
                healthConnectPermissionsNeeded = autorisationNeed!!,
                viewModel = healthViewModel
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                IconButton(onClick = { healthViewModel.syncHealthData() }) {
                    Icon(
                        Icons.Default.Sync,
                        "sync ",
                        tint = secondaryTextColor,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(4.dp)
                    )
                }
                Text(
                    stringResource(R.string.reload),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            }
        }


    }
}