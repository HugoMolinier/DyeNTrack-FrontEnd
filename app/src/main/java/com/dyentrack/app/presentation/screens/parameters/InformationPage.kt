package com.dyentrack.app.presentation.screens.parameters

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
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.DatePickerInput
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.SingleCircleSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.type.EmphasisType
import com.dyentrack.app.presentation.screens.parameters.viewModel.AuthViewModel

@Composable
fun InformationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
) {

    val authState by authViewModel.authState.collectAsState()
    val requestState by authViewModel.requestState.collectAsState()
    if (!authState.isConnected) navController.navigate("parametre")


    val isLoading = requestState is RequestState.Loading

    val originalPseudo = remember { mutableStateOf(authState.user?.pseudo ?: "") }
    val originalHeight = remember { mutableStateOf(authState.user?.height?.toString() ?: "170") }
    val originalBirthday = remember { mutableStateOf(authState.user?.birthdate ?: "") }
    val originalSexe = remember { mutableStateOf(authState.user?.sexe) }

    //  Valeurs modifiables
    val pseudo = remember { mutableStateOf(TextFieldValue(originalPseudo.value)) }
    val height = remember { mutableStateOf(TextFieldValue(originalHeight.value)) }
    val birthday = remember { mutableStateOf(originalBirthday.value) }
    val sexe = remember { mutableStateOf(originalSexe.value) }
    val isEditing = remember { mutableStateOf(false) }


    val errorText =  when ((requestState as? RequestState.Error)?.error) {
        AuthError.NOT_PASSWORD -> "Format mot de passe invalide"
        AuthError.NOT_EMAILFORMAT -> "Format email invalide"
        AuthError.UNAUTHORIZED-> "Identifiants incorrects"
        AuthError.INVALID_ARGUMENT -> "Identifiants incorrects"
        AuthError.ERROR_API -> "Erreur serveur"
        null -> ""
        AuthError.NOT_PSEUDOFORMAT -> "Format pseudo invalide, 3 à 20 caractères"
        AuthError.NOT_HEIGHTFORMAT -> "Taille entre 50 et 300 cm"
    }
    if (isEditing.value &&requestState is RequestState.Success ) {
        isEditing.value = false
        authViewModel.clearRequestState()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Information",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("parametre") },
                rightSide = { Icon(Icons.Default.Close, "Retourner au menu",modifier=Modifier.clickable(onClick = {navController.navigate("home")}))})

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp)
                    .weight(1f),
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            "Pseudo",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        InputCustom(
                            text = pseudo, variant = InputVariant.OUTLINED,
                            placeholder = "pseudo",
                            enabled = isEditing.value || isLoading
                        )
                    }
                }
                item {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            "Taille",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        InputCustom(
                            text = height,
                            placeholder = "taille",
                            variant = InputVariant.OUTLINED,
                            maxCaracter = 3,
                            keyboardType = KeyboardType.Number,
                            enabled = isEditing.value || isLoading


                        )
                    }


                }

                item {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            "Anniversaire",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        DatePickerInput(birthday,isEditing.value || isLoading)
                    }


                }
                item {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {

                    }
                }
                item {
                    Column (verticalArrangement = Arrangement.spacedBy(8.dp)){
                        Text(
                            "Sexe",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.Absolute.spacedBy(40.dp),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Homme",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                SingleCircleSelector(
                                    isSelected = sexe.value == true,

                                    onClick = { sexe.value = true },
                                    isEditing.value || isLoading

                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Femme",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                SingleCircleSelector(
                                    isSelected = sexe.value == false,
                                    onClick = { sexe.value = false },
                                    isEditing.value || isLoading
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Autre",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                SingleCircleSelector(
                                    isSelected = sexe.value == null,
                                    onClick = { sexe.value = null },
                                    isEditing.value || isLoading
                                )
                            }

                        }
                    }


                }

                item {
                    Box(modifier= Modifier.padding(top = 8.dp, bottom = 12.dp, start = 8.dp)) {

                        Row (horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically){
                            if (errorText!="")Icon(Icons.Outlined.ErrorOutline,"Error", tint = Color.Red ,modifier= Modifier.size(14.dp))
                            Text(
                                text = errorText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }


                }



            }

            Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(bottom=12.dp)) {
                if(isEditing.value) {
                    Row(modifier= Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        CustomButton(
                            label = "Annuler",
                            onClick = {
                                pseudo.value = TextFieldValue(originalPseudo.value)
                                height.value = TextFieldValue(originalHeight.value)
                                birthday.value = originalBirthday.value
                                sexe.value = originalSexe.value
                                isEditing.value = false
                                authViewModel.clearRequestState()
                            },
                            emphasis = EmphasisType.SECONDARY,
                            modifier = Modifier.weight(1f),
                            enable = requestState !is RequestState.Loading
                        )
                        CustomButton(
                            label = if(isLoading) "Appliquer..." else "Appliquer",
                            onClick = {
                                authViewModel.updateUser(
                                    pseudo.value.text,
                                    height.value.text.toInt(),
                                    sexe.value,
                                    birthday.value
                                )
                            },
                            modifier = Modifier.weight(1f),
                            isLoading = requestState is RequestState.Loading,
                            enable = requestState !is RequestState.Loading
                        )
                    }
                }
                else{
                    Row(modifier= Modifier.padding(horizontal = 12.dp)) {
                        CustomButton(
                            label = "Modifier les infos",
                            onClick = {

                                isEditing.value = true
                            },
                            emphasis = EmphasisType.SECONDARY,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}