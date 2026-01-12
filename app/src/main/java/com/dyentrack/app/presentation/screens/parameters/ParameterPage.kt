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
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Rule
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.DatasetLinked
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.WbIncandescent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ParameterIcon
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.enums.EmphasisType
import com.dyentrack.app.domain.model.enums.SizeType
import com.dyentrack.app.domain.model.enums.VariantType
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.ParameterBox
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ParameterPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
) {


    val authState by authViewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                stringResource(R.string.parameter), rightSide = {
                    Icon(
                        Icons.Default.Close,
                        stringResource(R.string.back_to_menu),
                        modifier = Modifier.clickable(onClick = { navController.navigate("home") })
                    )
                })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp)
                    .weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                //debut composant
                if (!authState.isConnected) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text(
                                stringResource(R.string.signup_prompt),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Save, "saveIcon")
                                Text(
                                    stringResource(R.string.save_data),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Create, "saveIcon")
                                Text(
                                    stringResource(R.string.create_exercises),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.WbIncandescent, "saveIcon")
                                Text(
                                    stringResource(R.string.collect_achievements),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CustomButton(
                                    stringResource(R.string.signup),
                                    modifier = modifier.fillMaxWidth(),
                                    size = SizeType.MEDIUM,
                                    onClick = { navController.navigate("inscription") })
                                CustomButton(
                                    stringResource(R.string.login),
                                    variant = VariantType.OUTLINED,
                                    emphasis = EmphasisType.SECONDARY,
                                    modifier = modifier.fillMaxWidth(),
                                    size = SizeType.MEDIUM,
                                    onClick = { navController.navigate("connexion") })
                            }
                            HorizontalDivider(
                                color = secondaryTextColor.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding()
                            )
                        }
                    }


                }
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {


                        Text(
                            stringResource(R.string.profile),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        ParameterBox(
                            listOfNotNull(
                                if (authState.isConnected) ParameterItem(
                                    { navController.navigate("information") },
                                    stringResource(R.string.information),
                                    icon = ParameterIcon.Vector(Icons.Outlined.Person)
                                ) else null,
                                ParameterItem(
                                    { navController.navigate("connectedApp") },
                                    stringResource(R.string.connected_app),
                                    ParameterIcon.Vector(Icons.Outlined.DatasetLinked)
                                ),
                                ParameterItem(
                                    { navController.navigate("objectif") },
                                    stringResource(R.string.my_goals),
                                    ParameterIcon.Vector(Icons.AutoMirrored.Outlined.Rule)
                                ),
                            )
                        )

                    }

                }
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            stringResource(R.string.preferences_and_data),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        ParameterBox(
                            listOfNotNull(
                                ParameterItem(
                                    { navController.navigate("mesureUnit") },
                                    stringResource(R.string.measurement_unit),
                                    ParameterIcon.Vector(Icons.Outlined.Straighten)
                                ), ParameterItem(
                                    { navController.navigate("language") },
                                    stringResource(R.string.language),
                                    ParameterIcon.Vector(Icons.Default.Translate)
                                ), ParameterItem(
                                    { navController.navigate("advanceParam") },
                                    stringResource(R.string.advanced),
                                    ParameterIcon.Vector(Icons.Outlined.Memory)
                                )
                            )
                        )
                    }


                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            stringResource(R.string.other),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        ParameterBox(
                            listOf(
                                /* ParameterItem(
                                     { }, stringResource(R.string.accessibility),
                                     ParameterIcon.Vector(Icons.Outlined.Accessibility)
                                 ),*/
                                ParameterItem(
                                    { navController.navigate("privacy") },
                                    stringResource(R.string.privacy),
                                    ParameterIcon.Vector(Icons.AutoMirrored.Outlined.Assignment)
                                ),
                                ParameterItem(
                                    { navController.navigate("legal") },
                                    stringResource(R.string.legal_notices),
                                    ParameterIcon.Vector(Icons.AutoMirrored.Outlined.Assignment)
                                ),
                            )
                        )
                    }


                }
                if (authState.isConnected) {
                    item {
                        Button(
                            onClick = { authViewModel.logout();navController.navigate("parametre") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = secondaryTextColor
                            )
                        ) {
                            Text(
                                stringResource(R.string.logout),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}