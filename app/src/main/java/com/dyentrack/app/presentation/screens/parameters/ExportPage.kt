package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.AuthError
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.presentation.components.CenterMenu
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.DatePickerInput
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.ParameterBox
import com.dyentrack.app.presentation.components.SingleCircleSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ExportPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
) {
    val authState by authViewModel.authState.collectAsState()
    val startDate = remember { mutableStateOf("") }
    val endDate = remember { mutableStateOf("") }
    val isFullExport = remember { mutableStateOf(true) }
    val requestState by authViewModel.requestState.collectAsState()
    val errorText = when ((requestState as? RequestState.Error)?.error) {
        AuthError.UNAUTHORIZED -> stringResource(R.string.error_not_connected)
        AuthError.ERROR_API -> stringResource(R.string.error_api)
        AuthError.INVALID_ARGUMENT -> stringResource(R.string.error_invalid_dates)
        else -> ""
    }

    val successText = stringResource(R.string.success)
    val successdesText = stringResource(R.string.export_success)
    val successButtonText = stringResource(R.string.export_success_button)
    if (!authState.isConnected) {
        navController.navigate("parametre")
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                text = stringResource(R.string.export_data),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() },
                rightSide = {
                    Icon(
                        Icons.Default.Close,
                        stringResource(R.string.back_to_menu),
                        modifier = Modifier.clickable { navController.navigate("home") })
                })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.export_mode),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        val menu = listOf(
                            ParameterItem(
                                onClick = {
                                    isFullExport.value = true
                                    startDate.value = ""
                                    endDate.value = ""
                                },
                                label = stringResource(R.string.export_full),
                                sublabel = null,
                                composableRight = {
                                    SingleCircleSelector(
                                        isSelected = isFullExport.value,
                                        onClick = {
                                            isFullExport.value = true
                                            startDate.value = ""
                                            endDate.value = ""
                                        },
                                        enabled = !isFullExport.value
                                    )
                                }
                            ),
                            ParameterItem(
                                onClick = {
                                    isFullExport.value = false
                                },
                                label = stringResource(R.string.export_partial),
                                sublabel = null,
                                composableRight = {
                                    SingleCircleSelector(
                                        isSelected = !isFullExport.value,
                                        onClick = { isFullExport.value = false },
                                        enabled = isFullExport.value
                                    )
                                }
                            )
                        )

                        ParameterBox(menu)


                        AnimatedVisibility(
                            visible = !isFullExport.value,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }), // vient du haut
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.padding(top = 20.dp)
                            ) {

                                Text(
                                    text = stringResource(R.string.export_period),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {

                                    DatePickerInput(
                                        modifier.weight(1f),
                                        birthday = startDate,
                                        enable = true,
                                        text = stringResource(R.string.start_date)
                                    )
                                    Text(
                                        "-",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = secondaryTextColor,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    DatePickerInput(
                                        modifier.weight(1f),
                                        birthday = endDate,
                                        enable = true,
                                        text = stringResource(R.string.end_date)
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (errorText.isNotBlank()) Icon(
                                Icons.Outlined.ErrorOutline,
                                errorText,
                                tint = Color.Red,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = errorText, style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red, fontSize = 12.sp
                                )
                            )
                        }

                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.information),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {

                            Text(
                                text = stringResource(R.string.excel_generate_info),
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                            )

                            Text(
                                text = stringResource(R.string.excel_generate_details),
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                CustomButton(
                    label = if (requestState is RequestState.Loading)
                        stringResource(R.string.get_loading)
                    else
                        stringResource(R.string.get),
                    onClick = {
                        authViewModel.requestDataUser(
                            startDate = startDate.value,
                            endDate = endDate.value,
                            isFullExport.value
                        )
                    },
                    modifier = Modifier.weight(1f),
                    isLoading = requestState is RequestState.Loading,
                    enable = requestState !is RequestState.Loading
                )
            }

        }
        if (requestState == RequestState.Success) {
            CenterMenu(
                title = successText,
                descrition = successdesText,
                items = listOf(MenuItemData(successButtonText,
                    { navController.navigate("parametre") }))


            )
        }
    }
}