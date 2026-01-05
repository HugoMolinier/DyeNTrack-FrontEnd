package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.AppViewModel
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ConnexionPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    appViewModel: AppViewModel = hiltViewModel<AppViewModel>(),
) {


    val authState by authViewModel.authState.collectAsState()
    val requestState by authViewModel.requestState.collectAsState()

    if (authState.isConnected) {
        appViewModel.syncUserData()
        navController.navigate("parametre")
    }

    val email = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }
    val isLoading = requestState is RequestState.Loading
    val errorText = when ((requestState as? RequestState.Error)?.error) {
        AuthError.NOT_PASSWORD -> stringResource(R.string.error_invalid_password)
        AuthError.NOT_EMAILFORMAT -> stringResource(R.string.error_invalid_email)
        AuthError.UNAUTHORIZED -> stringResource(R.string.error_email_used)
        AuthError.INVALID_ARGUMENT -> stringResource(R.string.error_email_used)
        AuthError.ERROR_API -> stringResource(R.string.error_api)
        else -> ""
    }
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) {
        Column {
            TopNavigationBar(
                stringResource(R.string.login),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("parametre") },
                rightSide = {
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
                    .weight(1f),
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            stringResource(R.string.email_label),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        InputCustom(
                            text = email, variant = VariantType.OUTLINED,
                            placeholder = stringResource(R.string.email_placeholder)
                        )
                    }
                }
                item {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            stringResource(R.string.password_label),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        InputCustom(
                            text = password,
                            placeholder = stringResource(R.string.password_placeholder),
                            variant = VariantType.OUTLINED,
                            hideContent = true
                        )
                    }


                }

                item {
                    Box(modifier = Modifier.padding(top = 8.dp, bottom = 12.dp, start = 8.dp)) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (errorText != "") Icon(
                                Icons.Outlined.ErrorOutline,
                                "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(14.dp)
                            )
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
                item {
                    CustomButton(
                        label = if (isLoading) stringResource(R.string.logging_in) else stringResource(R.string.signup),
                        onClick = { authViewModel.login(email.value.text, password.value.text) },
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = isLoading,
                        enable = !isLoading
                    )

                }
                item {
                    Row(
                        modifier = Modifier
                            .padding(top = 28.dp, start = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        Text(
                            text =stringResource(R.string.no_account),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = secondaryTextColor,
                            )
                        )
                        Text(
                            text = stringResource(R.string.signup_here),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable(onClick = { navController.navigate("inscription") })
                        )
                    }

                }

            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.hello),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.scale(0.9f)
                )
            }

        }
    }
}