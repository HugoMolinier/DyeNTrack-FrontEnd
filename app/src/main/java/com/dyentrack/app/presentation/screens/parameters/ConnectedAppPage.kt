package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.SingleCircleSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.type.EmphasisType
import com.dyentrack.app.presentation.components.type.SizeType
import com.dyentrack.app.presentation.globalViewModel.HealthConnectEtat
import com.dyentrack.app.presentation.globalViewModel.HealthConnectViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.screens.seance.dataSection.component.HealthConnectPermissionsScreen
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ConnectedAppPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
    healthViewModel: HealthConnectViewModel = hiltViewModel<HealthConnectViewModel>()
) {
    val context = LocalContext.current
    val healthEtat by healthViewModel.etat.collectAsState()
    val userPreference by userPreferenceViewModel.userPreference.collectAsState()
    val autorisationNeed by healthViewModel.autorisationNeed.collectAsState()
    val textHealthStatus = when (healthEtat) {
        HealthConnectEtat.VERIFICATION -> "Vérification..."
        HealthConnectEtat.INSTALLER -> "Installer"
        HealthConnectEtat.AUTORISER -> "Autoriser"
        HealthConnectEtat.OPERATIONNEL -> "Opérationnel"
        HealthConnectEtat.METTRE_A_JOUR -> "Mettre à jour"
        HealthConnectEtat.NON_DISPONIBLE -> "Non disponible"
    }
    if (autorisationNeed?.isNotEmpty() == true) {
        HealthConnectPermissionsScreen(
            healthConnectPermissionsNeeded = autorisationNeed!!, viewModel = healthViewModel
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Applicaton Lié",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("parametre") },
                rightSide = {
                    Icon(
                        Icons.Default.Close,
                        "Retourner au menu",
                        modifier = Modifier.clickable(onClick = { navController.navigate("home") })
                    )
                })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.health_connect_logo_white),
                                contentDescription = "Logo Health Connect",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Health Connect", modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Bouton pour le status de l'app
                        CustomButton(
                            label = textHealthStatus,
                            size = SizeType.SMALL,
                            emphasis = EmphasisType.SECONDARY,
                            onClick = { healthViewModel.onButtonClick(context) })
                    }


                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Information",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            "Cette application permet de suivre ton activité, ta nutrition et ton sommeil.\n \n" + "Nous lisons les informations nécessaires : pas, nutrition, poids et sommeil. Tes séances d’exercices seront enregistrées pour que d'autre application le voit..",
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                        )
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Auto-Sync", style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            horizontalArrangement = Arrangement.Absolute.spacedBy(40.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Oui",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                SingleCircleSelector(
                                    isSelected = userPreference?.autoSync == true, onClick = {
                                        userPreferenceViewModel.updateUserPreference(
                                            UserPreference(autoSync = true)
                                        )
                                    }, userPreference?.autoSync == false

                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Non",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                SingleCircleSelector(
                                    isSelected = userPreference?.autoSync == false, onClick = {
                                        userPreferenceViewModel.updateUserPreference(
                                            UserPreference(autoSync = false)
                                        )
                                    }, userPreference?.autoSync == true
                                )
                            }
                        }
                        Text(
                            "Syncronisation a chaque démarage si opérationnel et autorisé",
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                    }
                }
            }
        }
    }

}