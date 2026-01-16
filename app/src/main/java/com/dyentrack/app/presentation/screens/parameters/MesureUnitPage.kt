package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.presentation.components.ParameterBox
import com.dyentrack.app.presentation.components.SingleCircleSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun MesureUnitPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
) {
    val focusManager = LocalFocusManager.current
    val userPreference by userPreferenceViewModel.userPreference.collectAsState()

    val unitOptions = listOf(
        "Kilo (kg)" to MesureUnit.KG, "Pound (lbs)" to MesureUnit.LBS
    )


    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }) {
        Column {
            TopNavigationBar(
                text=stringResource(R.string.measurement_unit),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() },
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
                    .padding(start = 12.dp, end = 12.dp, top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {

                        Text(
                            stringResource(R.string.set_display_unit),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        val menu = unitOptions.map { (label, unit) ->
                            val isSelected = userPreference?.mesureUnit == unit

                            ParameterItem(onClick = {
                                if (!isSelected) {
                                    userPreferenceViewModel.updateUserPreference(
                                        UserPreference(mesureUnit = unit)
                                    )
                                }
                            }, label = label, sublabel = null, composableRight = {
                                SingleCircleSelector(
                                    isSelected = isSelected, onClick = {
                                        if (!isSelected) {
                                            userPreferenceViewModel.updateUserPreference(
                                                UserPreference(mesureUnit = unit)
                                            )
                                        }
                                    }, enabled = !isSelected
                                )
                            })
                        }
                        ParameterBox(menu)

                    }

                }
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 20.dp, start = 8.dp)
                    ) {
                        Text(
                            stringResource(R.string.information),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Column {
                            Text(
                                stringResource(R.string.info_measure_unit_1),
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor),
                            )
                            Text(
                                stringResource(R.string.info_measure_unit_2),
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor),
                            )
                            Text(
                                stringResource(R.string.info_measure_unit_3),
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor),
                                modifier = Modifier.padding(top = 12.dp)
                            )

                        }

                        Row(modifier = Modifier.padding(start = 24.dp, top = 4.dp)) {
                            Text("• ", color = secondaryTextColor)
                            Text(
                                "Pound -> Kilo : " + stringResource(R.string.weight) + " x 0.45359237",
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                            )
                        }
                        Row(modifier = Modifier.padding(start = 24.dp, bottom = 2.dp)) {
                            Text("• ", color = secondaryTextColor)
                            Text(
                                "Kilo -> Pound : " + stringResource(R.string.weight) + " x 2.20462262185",
                                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                            )
                        }
                    }
                }

            }
        }
    }

}