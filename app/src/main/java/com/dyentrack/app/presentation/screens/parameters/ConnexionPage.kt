package com.dyentrack.app.presentation.screens.parameters

import android.graphics.Color.red
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.home.HomeViewModel
import com.dyentrack.app.presentation.screens.parameters.viewModel.AuthViewModel

@Composable
fun ConnexionPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
) {

    val authResponse by authViewModel.authResponse.collectAsState()

    if (authResponse?.status== AuthStatus.CONNECTED) navController.popBackStack()

    val email = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Paramètre",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() },
                rightSide = { Icon(Icons.Default.Close, "Retourner au menu") })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text("Email")
                    InputCustom(
                    text = email, placeholder = "Email", variant = InputVariant.OUTLINED)}
                item {
                    Text("Mot de passe")
                    InputCustom(
                        text = password,
                        placeholder = "Password",
                        variant = InputVariant.OUTLINED,
                        hideContent = true
                    )



                }

                item {
                    authResponse?.error?.let { error ->
                        when (error) {
                            AuthError.NOT_PASSWORD -> Text(
                                "Format mot de passe invalide",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            )
                            AuthError.NOT_EMAILFORMAT -> Text(
                                "Format email invalide",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            )
                            AuthError.UNAUTHORIZED -> Text(
                                "Identifiants incorrects",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            )
                            AuthError.ERROR_API -> Text(
                                "Erreur serveur",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            )
                            AuthError.INVALID_ARGUMENT -> Text(
                                "Identifiants incorrects",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            )
                            null -> {}

                        }
                    }

                }
                item {
                    CustomButton (
                        label = "Connexion" ,
                        onClick = {authViewModel.login(email.value.text,password.value.text)},
                        modifier = Modifier.fillMaxWidth()
                    )

                }

            }

        }
    }
}