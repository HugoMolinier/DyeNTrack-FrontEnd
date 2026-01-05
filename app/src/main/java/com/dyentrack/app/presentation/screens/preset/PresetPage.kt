package com.dyentrack.app.presentation.screens.preset

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.domain.model.enum.SizeType
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.presentation.components.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.ParameterBox
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.globalViewModel.InfoUserViewModel
import com.dyentrack.app.presentation.globalViewModel.PresetViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.ExerciseImages
import kotlinx.coroutines.launch

@Composable
fun PresetPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    idLocal: Long,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    presetPageViewModel: PresetPageViewModel = hiltViewModel<PresetPageViewModel>(),
    presetViewModel: PresetViewModel = hiltViewModel<PresetViewModel>(),
    infoUserViewModel: InfoUserViewModel = hiltViewModel<InfoUserViewModel>(),
) {

    val state by presetPageViewModel.uiState.collectAsState()
    val allPreset by presetViewModel.presetFlow.collectAsState()
    LaunchedEffect(idLocal, allPreset) {
        allPreset.find { it.idLocal == idLocal }?.let { preset ->
            presetPageViewModel.setPreset(preset)
        }
    }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val textToast = stringResource(R.string.favorite_login_prompt)
    val language = presetPageViewModel.languageUser
    val coroutineScope = rememberCoroutineScope()
    val infoMapUser by infoUserViewModel.infoExerciseUserFlow.collectAsState()
    val masterData by presetPageViewModel.masterData.collectAsState()
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column {
            TopNavigationBar(
                "Preset",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                rightSide = {
                    Icon(Icons.Outlined.Save, "Save Preset",modifier= Modifier.clickable(enabled = state.currentPreset?.presetSeanceExerciseVO?.isNotEmpty()==true, onClick = {
                        state.currentPreset?.let { preset ->
                            coroutineScope.launch {
                                presetViewModel.savePreset(preset)
                            }
                        }
                    }))
                },
                onClickLeft = { navController.popBackStack() })


            LazyColumn(
                modifier = Modifier
                    .weight(1f),
            ) {

                state.currentPreset?.let {
                    item {
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .border(
                                        width = 1.dp,
                                        color = secondaryTextColor,
                                        shape = MaterialTheme.shapes.extraLarge
                                    ),
                                contentAlignment = Alignment.Center
                            ) {}
                            Column(modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)) {
                                Text(it.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Future description",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                )
                                Row {
                                    Text(
                                        text = (it.presetSeanceExerciseVO?.size
                                            ?: 0).toString() + "exercise | ",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )
                                    val sumSet=it.presetSeanceExerciseVO?.sumOf { it.nbSet }?:0
                                    Text(
                                        text =  ("${sumSet} " + pluralStringResource(
                                                R.plurals.set,
                                            sumSet
                                            ) + " | "),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )
                                    val totalMinutes = sumSet * 3

                                    Text(
                                        text = "≈ ${totalMinutes / 60}h${String.format("%02d", totalMinutes % 60)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )


                                }
                            }

                        }

                    }
                    item {
                        HorizontalDivider(
                            color = secondaryTextColor.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    val exercises = state.currentPreset!!.presetSeanceExerciseVO

                    if (exercises?.isNotEmpty() == true) {
                        itemsIndexed(
                            exercises,
                            key = { _, ex -> ex.idExercise }) { _, exInPreset ->

                            Box(
                                modifier = Modifier
                                    .padding(10.dp, 4.dp)
                            ) {
                                CardWithContent(
                                    modifier = Modifier.padding(10.dp, 4.dp),
                                    state.masterDataMaps?.exerciseMap?.get(exInPreset.idExercise)
                                        ?.getName(language) ?: "",
                                    subText = "${exInPreset.nbSet} " + pluralStringResource(R.plurals.set,exInPreset.nbSet) + " x " +"${exInPreset.rangeRepInf?:""} - ${exInPreset.rangeRepSup?:""}",
                                    leftIcon = LeftIcon.Single(
                                        ExerciseImages.map[exInPreset.idExercise]
                                            ?: R.drawable.logotransparent
                                    ),
                                    menuOnLongClick = listOf(
                                        MenuItemData(
                                            stringResource(R.string.exercise_view),
                                            {
                                                navController.navigate("exercise/${exInPreset.idExercise}")
                                            }

                                        )
                                    ),
                                    onExpandChange = { expanded ->
                                        presetPageViewModel.expandExercise(if (expanded) exInPreset.idExercise else null)
                                    },
                                    componentRight = {
                                        Icon(
                                            if (state.expandedExerciseId == exInPreset.idExercise) {
                                                Icons.Default.ArrowDropUp
                                            } else {
                                                Icons.Default.ArrowDropDown
                                            },
                                            ""
                                        )
                                    },
                                    canExtend=true,
                                    expanded = state.expandedExerciseId == exInPreset.idExercise,
                                    childrenForExtend = {
                                        val repMinState = remember {
                                            mutableStateOf(
                                                TextFieldValue(
                                                    exInPreset.rangeRepInf?.toString().orEmpty()
                                                )
                                            )
                                        }
                                        val repMaxState = remember {
                                            mutableStateOf(TextFieldValue(exInPreset.rangeRepSup?.toString().orEmpty()))
                                        }
                                        val list= listOf(ParameterItem(label = "Nombre de série", composableRight = {
                                            Row(modifier.height(40.dp), verticalAlignment = Alignment.CenterVertically) {
                                                CustomButton("-", emphasis = EmphasisType.TERTIA, modifier = Modifier.width(40.dp),size= SizeType.SMALL,     enable = (exInPreset.nbSet ) > 1, onClick = {presetPageViewModel.changeNbSet(exInPreset.idExercise, -1)})
                                                Text(exInPreset.nbSet.toString(),modifier=Modifier.padding(horizontal = 20.dp))
                                                CustomButton("+", emphasis = EmphasisType.TERTIA, modifier = Modifier.width(40.dp),size= SizeType.SMALL,enable = (exInPreset.nbSet ) < 20, onClick = {presetPageViewModel.changeNbSet(exInPreset.idExercise, +1)})

                                            }
                                        }),
                                            ParameterItem(label = "Fourchette de rep", composableRight = {
                                                Row(
                                                    modifier = Modifier.height(40.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {

                                                    InputCustom(
                                                        size = SizeType.NO,
                                                        singleLine = true,
                                                        modifier = Modifier.size(40.dp),
                                                        maxCaracter = 2,
                                                        text = repMinState,
                                                        keyboardType = KeyboardType.Number,
                                                        centerText = true,
                                                        onFocusLost = { value ->
                                                            presetPageViewModel.updateExerciseRange(
                                                                exerciseId = exInPreset.idExercise,
                                                                rangeRepInf = value.toIntOrNull(),
                                                                rangeRepSup = repMaxState.value.text.toIntOrNull()

                                                            )
                                                            val updatedEx = presetPageViewModel.uiState.value.currentPreset?.presetSeanceExerciseVO
                                                                ?.find { it.idExercise == exInPreset.idExercise }

                                                            repMinState.value = TextFieldValue((updatedEx?.rangeRepInf?:"").toString())
                                                            repMaxState.value = TextFieldValue((updatedEx?.rangeRepSup?:"").toString())
                                                        }
                                                    )

                                                    Text(
                                                        "à",
                                                        modifier = Modifier.padding(horizontal = 20.dp),
                                                        style=MaterialTheme.typography.bodyMedium
                                                    )

                                                    InputCustom(
                                                        size = SizeType.NO,
                                                        modifier = Modifier.size(40.dp),
                                                        singleLine = true,
                                                        maxCaracter = 2,
                                                        centerText = true,
                                                        text = repMaxState,
                                                        keyboardType = KeyboardType.Number,
                                                        onFocusLost = { value ->
                                                            presetPageViewModel.updateExerciseRange(
                                                                exerciseId = exInPreset.idExercise,
                                                                rangeRepInf = repMinState.value.text.toIntOrNull(),
                                                                rangeRepSup =  value.toIntOrNull(),
                                                            )
                                                            val updatedEx = presetPageViewModel.uiState.value.currentPreset?.presetSeanceExerciseVO
                                                                ?.find { it.idExercise == exInPreset.idExercise }

                                                            repMinState.value = TextFieldValue((updatedEx?.rangeRepInf?:"").toString())
                                                            repMaxState.value = TextFieldValue((updatedEx?.rangeRepSup?:"").toString())
                                                        }
                                                    )
                                                }
                                            }))
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                                        ) {

                                                ParameterBox(list, PaddingValues(top=16.dp))

                                        }
                                    }
                                )
                            }
                        }


                    }
                }
            }


        }
        FloatingActionButton(

            onClick = {
                presetPageViewModel.setExerciseToAdd()
                presetPageViewModel.toggleBottomSheet(BottomSheetVisibleType.ONLYEXERCICE)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 16.dp)
                .size(58.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.add_exercise),
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(12.dp)
            )

        }
    }


    BottomSheetSelectionExercise(
        bottomSheetVisibility = state.isBottomSheetVisible == BottomSheetVisibleType.ONLYEXERCICE,
        selectedExerciseIds = state.exerciseToAdd,
        navController = navController,
        title = stringResource(R.string.add_exercise),
        onClose = { presetPageViewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        onClickExercise = { exercise ->
            presetPageViewModel.toggleExerciseSelection(exercise.idExercise)
        },
        officialExercise = masterData?.officialExercise,
        infoMapUser = infoMapUser,
        onClickFavorite = {
            if (authState.isConnected) infoUserViewModel.updateInfoExerciseUser(
                it,
                !(infoMapUser[it]?.favorite ?: false),
                null
            ) else {
                Toast.makeText(context, textToast, Toast.LENGTH_SHORT).show()
            }
        },
        userlanguage = language,
        musclesGroupMap = state.masterDataMaps?.musclesGroupMap
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CustomButton(
                label = stringResource(R.string.cancel),
                emphasis = EmphasisType.SECONDARY,
                modifier = Modifier.weight(1f),
                onClick = { presetPageViewModel.toggleBottomSheet(BottomSheetVisibleType.NO) }
            )
            CustomButton(
                label = stringResource(R.string.add),
                modifier = Modifier.weight(1f),
                onClick = { presetPageViewModel.addExerciseToSeance() }
            )
        }
    }

}





