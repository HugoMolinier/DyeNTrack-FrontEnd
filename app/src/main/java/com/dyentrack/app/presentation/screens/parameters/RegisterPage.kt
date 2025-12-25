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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.parameters.viewModel.AuthViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
) {

    val authResponse by authViewModel.authResponse.collectAsState()

    if (authResponse?.status== AuthStatus.CONNECTED) navController.navigate("parametre")

    val email = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }
    val pseudo = remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Inscription",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("parametre") },

                rightSide = { Icon(Icons.Default.Close, "Retourner au menu",modifier=Modifier.clickable(onClick = {navController.navigate("home")})) })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp)
                    .weight(1f),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=12.dp)) {
                        Text("Email", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 4.dp))
                        InputCustom(
                            text = email, variant = InputVariant.OUTLINED,
                            placeholder = "email"
                        )
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=12.dp)) {
                        Text("Pseudo", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 4.dp))
                        InputCustom(
                            text = pseudo, variant = InputVariant.OUTLINED,
                            placeholder = "pseudo"
                        )
                    }
                }
                item {

                    Column (verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top=20.dp)){
                        Text("Mot de passe",style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(start = 8.dp))
                        InputCustom(
                            text = password,
                            placeholder = "mot de passe",
                            variant = InputVariant.OUTLINED,
                            hideContent = true
                        )
                    }



                }

                item {
                    Box(modifier= Modifier.padding(top = 8.dp, bottom = 12.dp, start = 8.dp)) {
                        val errorText = when (authResponse?.error) {
                            AuthError.NOT_PASSWORD -> "Format mot de passe invalide, 8 à 60 caractères (contenant 1 chiffre et 1 lettre)"
                            AuthError.NOT_EMAILFORMAT -> "Format email invalide"
                            AuthError.UNAUTHORIZED-> "Identifiants incorrects"
                            AuthError.INVALID_ARGUMENT -> "Identifiants incorrects"
                            AuthError.ERROR_API -> "Identifiants déjà utilisé"
                            null -> ""
                            AuthError.NOT_PSEUDOFORMAT -> "Format pseudo invalide, 3 à 20 caractères"
                        }

                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        )
                    }

                }
                item {
                    CustomButton (
                        label = "Finaliser l'inscription" ,
                        onClick = {authViewModel.register(email.value.text,password.value.text,pseudo.value.text)},
                        modifier = Modifier.fillMaxWidth()
                    )

                }
                item {
                    Row(modifier= Modifier.padding(top = 28.dp,start = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    )) {
                        Text(
                            text = "Déjà un compte ?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = secondaryTextColor,
                            )
                        )
                        Text(
                            text = "Connectez-vous ici",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier= Modifier.clickable(onClick = {navController.navigate("connexion")})
                        )
                    }

                }

            }


        }
    }
}