package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.core.utils.findActivity
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.presentation.components.ParameterBox
import com.dyentrack.app.presentation.components.SingleCircleSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel

@Composable
fun LanguagePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
) {
    val focusManager = LocalFocusManager.current
    val userPreference by userPreferenceViewModel.userPreference.collectAsState()


    val selectedLanguage = remember { mutableStateOf(userPreference?.language == LANGUAGE.FR) }
    val context = LocalContext.current
    val activity = context.findActivity()

    key(userPreference?.language) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }) {
            Column {
                TopNavigationBar(
                    stringResource(R.string.language),
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
                        .padding(start = 12.dp, end = 12.dp, top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(48.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {

                            Text(
                                stringResource(R.string.set_display_language),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )

                            val menu1 = listOfNotNull(
                                ParameterItem(
                                    onClick = {
                                        userPreferenceViewModel.updateUserPreference(
                                            UserPreference(language = LANGUAGE.FR)
                                        );selectedLanguage.value = true;activity?.recreate()
                                    },
                                    "Francais (FR)",
                                    null,
                                    {
                                        SingleCircleSelector(
                                            selectedLanguage.value,
                                            onClick = {
                                                userPreferenceViewModel.updateUserPreference(
                                                    UserPreference(language = LANGUAGE.FR)
                                                );selectedLanguage.value = true;activity?.recreate()
                                            },
                                            enable = (!selectedLanguage.value)
                                        )

                                    }
                                ),

                                ParameterItem(
                                    onClick = {
                                        userPreferenceViewModel.updateUserPreference(
                                            UserPreference(language = LANGUAGE.EN)
                                        );selectedLanguage.value = false;activity?.recreate()
                                    },
                                    "English (EN)",
                                    null,
                                    {
                                        SingleCircleSelector(
                                            !selectedLanguage.value,
                                            onClick = {
                                                userPreferenceViewModel.updateUserPreference(
                                                    UserPreference(language = LANGUAGE.EN)
                                                );selectedLanguage.value = false; activity?.recreate()
                                            }, enable = (selectedLanguage.value)
                                        )

                                    }
                                ),

                                )
                            ParameterBox(menu1)

                        }

                    }

                }
            }
        }
    }
}