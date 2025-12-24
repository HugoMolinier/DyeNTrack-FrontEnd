package com.dyentrack.app.presentation.screens.parameters

import android.R
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
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Rule
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.DatasetLinked
import androidx.compose.material.icons.outlined.Download
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.type.EmphasisType
import com.dyentrack.app.presentation.components.type.SizeType
import com.dyentrack.app.presentation.components.type.VariantType
import com.dyentrack.app.presentation.screens.parameters.component.ParameterBox
import com.dyentrack.app.presentation.screens.parameters.viewModel.AuthViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ParameterPage (modifier: Modifier = Modifier, navController: NavController,
                   authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>()) {

    val authResponse by authViewModel.authResponse.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Paramètre",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() },
                rightSide = { Icon(Icons.Default.Close, "Retourner au menu") })

            LazyColumn(
                modifier = modifier.fillMaxWidth().padding(start = 12.dp,end=12.dp,top=8.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                //debut composant
                if (authResponse?.status== AuthStatus.NOT_CONNECTED) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top=12.dp)) {
                            Text(
                                "Inscrivez-vous pour profiter de fonctionnalité",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier= Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                            )
                            Row (horizontalArrangement = Arrangement.spacedBy(12.dp),           modifier= Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top=12.dp), verticalAlignment = Alignment.CenterVertically){
                                Icon(Icons.Outlined.Save, "saveIcon")
                                Text("Garder une sauvegarde des données sur un serveur",style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                            }
                        Row (horizontalArrangement = Arrangement.spacedBy(12.dp),           modifier= Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically){
                            Icon(Icons.Outlined.Create, "saveIcon")
                            Text("Crée des exercices, accéder au note et favorie",style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                        }
                        Row (horizontalArrangement = Arrangement.spacedBy(12.dp),           modifier= Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically){
                            Icon(Icons.Outlined.WbIncandescent, "saveIcon")
                            Text("Collectionner plein d'achievement", style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                        }

                        Column(           modifier = Modifier.fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomButton(
                                "S'inscire",
                                modifier=modifier.fillMaxWidth(),
                                size = SizeType.MEDIUM
                            )
                            CustomButton(
                                "Se connecter",
                                variant = VariantType.OUTLINED,
                                emphasis = EmphasisType.SECONDARY,
                                modifier=modifier.fillMaxWidth(),
                                size = SizeType.MEDIUM,
                                onClick = {navController.navigate("connexion")}
                            )
                        }
                        HorizontalDivider(color =  secondaryTextColor.copy(alpha = 0.15f), modifier = Modifier.fillMaxWidth().padding())
                        }
                    }


                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=20.dp)) {


                        Text(
                            "Profile",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        val menu1 = listOfNotNull(
                            if (authResponse?.status== AuthStatus.CONNECTED) ParameterItem(
                                { },
                                "Informations",
                                Icons.Outlined.Person
                            ) else null,
                            ParameterItem({ }, "App Connecter", Icons.Outlined.DatasetLinked),
                            ParameterItem({ }, "Mes Objectifs", Icons.AutoMirrored.Outlined.Rule),
                        )
                        ParameterBox(menu1)

                    }

                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=20.dp)) {
                        Text("Préférence et données", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 8.dp))
                        val menu1 = listOfNotNull(
                            ParameterItem({ }, "Unité de mesures", Icons.Outlined.Straighten),
                            ParameterItem({ }, "Langue", Icons.Default.Translate),
                            if (authResponse?.status== AuthStatus.CONNECTED)
                                ParameterItem({ }, "Export des données", Icons.Outlined.Download)
                            else null
                        )
                        ParameterBox(menu1)
                    }


                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=20.dp)) {
                        Text("Autre", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 8.dp))
                        val menu1 = listOf(
                            ParameterItem({ }, "Accessibilité",
                                Icons.Outlined.Accessibility
                            ),
                            ParameterItem({ }, "Confidentialité", Icons.AutoMirrored.Outlined.Assignment),
                            ParameterItem({ }, "Mention Légales", Icons.AutoMirrored.Outlined.Assignment),
                        )
                        ParameterBox(menu1)
                    }


                }

                item{
                    Button(onClick = {authViewModel.logout()},modifier= Modifier.fillMaxWidth(),     colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = secondaryTextColor
                    )) { Text("Déconnection",style = MaterialTheme.typography.bodySmall)}
                }
            }
        }
    }
}