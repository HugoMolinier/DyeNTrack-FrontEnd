package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ParameterIcon
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.model.enums.SizeType
import com.dyentrack.app.domain.model.enums.VariantType
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.ParameterBox
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel

@Composable
fun ObjectifPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
) {
    val focusManager = LocalFocusManager.current
    val userPreference by userPreferenceViewModel.userPreference.collectAsState()


    val calorieText = remember {
        mutableStateOf(
            TextFieldValue(
                text = userPreference?.objCalorie?.toString() ?: "",
            )
        )
    }
    val stepText = remember {
        mutableStateOf(
            TextFieldValue(
                text = userPreference?.objStep?.toString() ?: "",
            )
        )
    }

    val sleepText = remember {
        mutableStateOf(
            TextFieldValue(
                text = userPreference?.objHourSleep?.toString() ?: "",
            )
        )
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }) {
        Column {
            TopNavigationBar(
                stringResource(R.string.my_goals),
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
                            stringResource(R.string.define_goals),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        val menu1 = listOfNotNull(
                            ParameterItem(
                            null,
                            stringResource(R.string.calories),
                            ParameterIcon.Resource(R.drawable.icon_cal),
                            {
                                InputCustom(
                                    modifier = Modifier.width(80.dp),
                                    text = calorieText,
                                    placeholder = "0",
                                    maxCaracter = 5,
                                    centerText = true,

                                    keyboardType = KeyboardType.Number,
                                    variant = VariantType.EMPTY,
                                    size = SizeType.SMALL,
                                    onValueChange = { value ->
                                        val intValue = value.toIntOrNull()?.coerceAtMost(9999) ?: 0
                                        userPreferenceViewModel.updateUserPreference(
                                            UserPreference(objCalorie = intValue)
                                        )
                                        calorieText.value = calorieText.value.copy(
                                            text = if (intValue != 0) intValue.toString() else ""
                                        )
                                    })

                            }),

                            ParameterItem(
                                null,
                                stringResource(R.string.number_steps),
                                ParameterIcon.Resource(R.drawable.icon_step),
                                {
                                    InputCustom(
                                        modifier = Modifier.width(80.dp),
                                        text = stepText,
                                        placeholder = "0",
                                        maxCaracter = 6,
                                        centerText = true,

                                        keyboardType = KeyboardType.Number,
                                        variant = VariantType.EMPTY,
                                        size = SizeType.SMALL,
                                        onValueChange = { value ->
                                            val intValue =
                                                value.toIntOrNull()?.coerceAtMost(99999) ?: 0
                                            userPreferenceViewModel.updateUserPreference(
                                                UserPreference(objStep = intValue)
                                            )
                                            stepText.value =
                                                stepText.value.copy(text = if (intValue != 0) intValue.toString() else "")
                                        })

                                }),

                            ParameterItem(
                                null,
                                stringResource(R.string.sleep_hours),
                                ParameterIcon.Resource(R.drawable.icon_sleep),
                                {
                                    InputCustom(
                                        modifier = Modifier.width(80.dp),
                                        text = sleepText,
                                        placeholder = "0.0",
                                        maxCaracter = 4,
                                        centerText = true,

                                        keyboardType = KeyboardType.Decimal,
                                        variant = VariantType.EMPTY,
                                        size = SizeType.SMALL,
                                        onValueChange = { value ->
                                            val limitedValue =
                                                value.toDoubleOrNull()?.takeIf { it != 0.0 }
                                                    ?.coerceIn(0.0, 24.0)

                                            userPreferenceViewModel.updateUserPreference(
                                                UserPreference(objHourSleep = limitedValue)
                                            )

                                            sleepText.value = sleepText.value.copy(
                                                text = if (value.isBlank() || value == ".") "" else limitedValue?.toString()
                                                    ?: ""
                                            )
                                        })

                                })

                        )
                        ParameterBox(menu1)

                    }

                }

            }
        }
    }

}