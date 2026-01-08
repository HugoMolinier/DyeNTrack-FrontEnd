package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.model.enum.SyncPerformanceMode
import com.dyentrack.app.presentation.components.SingleCircleSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun AdvanceParamPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
) {

    val userPreference by userPreferenceViewModel.userPreference.collectAsState()
    val syncOptions = listOf(
        "Tout" to SyncPerformanceMode.ALL,
        "Seulement Preset" to SyncPerformanceMode.PRESET_ONLY,
        "Rien" to SyncPerformanceMode.NONE
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Avancé",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() },
                rightSide = {
                    Icon(
                        Icons.Default.Close,
                        stringResource(R.string.back_to_menu),
                        modifier = Modifier.clickable { navController.navigate("home") }
                    )
                })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {

                        Text(
                            "Synchronisation automatique des performances",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            "Définit quand reprendre automatiquement la charge et le RIR depuis tes 15 séances précédentes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = secondaryTextColor,
                        )
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(40.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {

                        syncOptions.forEach { (label, mode) ->
                            val isSelected = userPreference?.syncPerformanceMode == mode
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                SingleCircleSelector(
                                    isSelected = isSelected,
                                    onClick = {
                                        userPreferenceViewModel.updateUserPreference(
                                            UserPreference(syncPerformanceMode = mode)
                                        )
                                    },
                                    enabled = !isSelected
                                )
                            }
                        }
                    }
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {

                        Text(
                            "Détails des modes de synchronisation",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            "1. Tout\n" +
                                    "La charge et le RIR sont automatiquement entré, pour tous les exercices ajoutés.",
                            style = MaterialTheme.typography.bodySmall,
                            color = secondaryTextColor
                        )

                        Text(
                            "2. Seulement presets\n" +
                                    "Les performances sont reprises uniquement lors de l’ajout d’exercices d'un même preset.",
                            style = MaterialTheme.typography.bodySmall,
                            color = secondaryTextColor
                        )

                        Text(
                            "3. Aucune\n" +
                                    "Aucune reprise automatique des performances. Les charges et le RIR restent vides.",
                            style = MaterialTheme.typography.bodySmall,
                            color = secondaryTextColor
                        )
                    }
                }


            }
        }
    }
}