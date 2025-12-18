package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.DatasetLinked
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.parameters.component.ParameterBox

@Composable
fun ParameterPage (modifier: Modifier = Modifier, navController: NavController) {





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
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=20.dp)) {
                        Text("Profile", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 8.dp))
                        val menu1 = listOf(
                            ParameterItem({ }, "Informations", Icons.Outlined.Person),
                            ParameterItem({ }, "App Connecter", Icons.Outlined.DatasetLinked),
                        )
                        ParameterBox(menu1)
                    }


                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=8.dp)) {
                        Text("Préférence et données", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 12.dp))
                        val menu1 = listOf(
                            ParameterItem({ }, "Unité de mesures", Icons.Outlined.Straighten),
                            ParameterItem({ }, "Langue", Icons.Default.Translate),
                            ParameterItem({ }, "Mes Objectifs",  Icons.AutoMirrored.Outlined.Rule),
                            ParameterItem({ }, "Export des données",
                                Icons.Outlined.Download
                            ),
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
            }
        }
    }
}