package com.dyentrack.app.presentation.screens.preset

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ActiveMenu
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.domain.model.enum.ExerciceInPropertyType
import com.dyentrack.app.domain.model.enum.ExerciseMode
import com.dyentrack.app.domain.model.enum.SizeType
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.presentation.components.BottomSheetInput
import com.dyentrack.app.presentation.components.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.CenterMenu
import com.dyentrack.app.presentation.components.ChipDropDown
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
import com.dyentrack.app.presentation.utils.formatSubtitle
import kotlin.math.roundToInt

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
    val infoMapUser by infoUserViewModel.infoExerciseUserFlow.collectAsState()
    val masterData by presetPageViewModel.masterData.collectAsState()
    val focusManager = LocalFocusManager.current
    val modeActual = state.exerciseMode
    var activeMenu by remember { mutableStateOf<ActiveMenu?>(null) }
    var expanded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            if ( state.currentPreset != null && state.isModified) {
                presetViewModel.savePreset(state.currentPreset!!)
            }
        }
    }
    BackHandler {
            if ( state.currentPreset != null && state.isModified) {
                presetViewModel.savePreset(state.currentPreset!!)
            }
        navController.popBackStack()
    }

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
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, "", tint = secondaryTextColor)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            containerColor = MaterialTheme.colorScheme.surface,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Changer le nom") },
                                onClick = {
                                    presetPageViewModel.toggleBottomSheet(
                                        BottomSheetVisibleType.INPUTPRESET
                                    )
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer") },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.Red
                                ),
                                onClick = {
                                    expanded = false
                                    activeMenu = ActiveMenu(
                                        title = "Supprimer le preset",
                                        description = "Cela dissociera tout les presets des seances associé ?",
                                        content = listOf(
                                            MenuItemData("Non", onClick = { activeMenu = null }),
                                            MenuItemData("Oui", color = Color.Red, onClick = {
                                                presetViewModel.deletePreset(state.currentPreset!!)
                                                activeMenu = null
                                                navController.popBackStack()

                                            }
                                            )
                                        )
                                    )
                                }
                            )
                        }
                    }
                },
                onClickLeft = {
                   navController.popBackStack()
                }
            )


            LazyColumn(
                modifier = Modifier
                    .weight(1f),
            ) {

                state.currentPreset?.let { currentPreset ->
                    item {
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(start = 16.dp, bottom = 12.dp, end = 20.dp, top = 8.dp),
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
                            Column(
                                modifier
                                    .padding(bottom = 4.dp)
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    currentPreset.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "Crée par vous",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                )
                                Row {
                                    Text(
                                        text = (currentPreset.presetSeanceExerciseVO?.size
                                            ?: 0).toString() + " exercise • ",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )
                                    val sumSet =
                                        currentPreset.presetSeanceExerciseVO?.sumOf { it.nbSet }
                                            ?: 0
                                    Text(
                                        text = ("$sumSet " + pluralStringResource(
                                            R.plurals.set,
                                            sumSet
                                        )),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )
                                    val totalMinutes = sumSet * 3

                                    Text(
                                        text = " ≈ ${totalMinutes / 60}h${
                                            String.format(
                                                "%02d",
                                                totalMinutes % 60
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )


                                }

                            }

                        }

                    }
                    item {
                        HorizontalDivider(
                            color = secondaryTextColor.copy(alpha = 0.2f),
                        )
                    }

                    item {
                        Row(
                            modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {

                                IconButton(
                                    onClick = {
                                        presetPageViewModel.toggleMode(ExerciseMode.NORMAL)
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = "Voir stats",
                                        tint = if (modeActual.isNormal()) MaterialTheme.colorScheme.primary else secondaryTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        presetPageViewModel.toggleMode(ExerciseMode.CHANGE_ORDER)
                                    },
                                ) {
                                    Icon(
                                        Icons.Outlined.SwapVert,
                                        contentDescription = "Voir stats",
                                        tint = if (modeActual.isChangeOrder()) MaterialTheme.colorScheme.primary else secondaryTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                    }
                    val exercises = state.currentPreset!!.presetSeanceExerciseVO

                    if (exercises?.isNotEmpty() == true) {
                        itemsIndexed(
                            exercises,
                            key = { _, ex -> ex.idExercise }) { _, exInPreset ->
                            var offsetY by remember { mutableFloatStateOf(0f) }

                            Box(
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset { IntOffset(0, offsetY.roundToInt()) }
                                    .then(
                                        if (state.exerciseMode.isChangeOrder()) {
                                            Modifier.pointerInput(exInPreset) {
                                                detectDragGestures(
                                                    onDragStart = { },
                                                    onDrag = { change, dragAmount ->
                                                        offsetY += dragAmount.y
                                                        change.consume()
                                                        val threshold = 70.dp.toPx()
                                                        val lastIndex = exercises.lastIndex


                                                        val currentIndex =
                                                            exercises.indexOf(exInPreset)

                                                        if (offsetY > threshold && currentIndex < lastIndex) {
                                                            presetPageViewModel.swapExercise(

                                                                currentIndex,
                                                                currentIndex + 1
                                                            )
                                                            offsetY = 0f
                                                        } else if (offsetY < -threshold && currentIndex > 0) {
                                                            presetPageViewModel.swapExercise(
                                                                currentIndex,
                                                                currentIndex - 1
                                                            )
                                                            offsetY = 0f
                                                        }
                                                    },
                                                    onDragEnd = {
                                                        offsetY = 0f

                                                    },
                                                    onDragCancel = {
                                                        offsetY = 0f

                                                    }
                                                )
                                            }
                                        } else Modifier
                                    )
                            ) {

                                CardWithContent(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        bottom = 4.dp,
                                        top = 4.dp
                                    ),
                                    state.masterDataMaps?.exerciseMap?.get(exInPreset.idExercise)
                                        ?.getName(language) ?: "",
                                    subText = exInPreset.formatSubtitle(
                                        setPlural = { count ->
                                            context.resources.getQuantityString(
                                                R.plurals.set,
                                                count,
                                                count
                                            )
                                        },
                                        repPlural = { count ->
                                            context.resources.getQuantityString(
                                                R.plurals.rep,
                                                count,
                                                count
                                            )
                                        }
                                    ),
                                    leftIcon = LeftIcon.Single(
                                        ExerciseImages.map[exInPreset.idExercise]
                                            ?: R.drawable.logotransparent
                                    ),
                                    hasLineShadowBetweenMainAndChildren = true,
                                    menuOnLongClick = listOf(
                                        MenuItemData(
                                            stringResource(R.string.exercise_view),
                                            { navController.navigate("exercise/${exInPreset.idExercise}") }
                                        ), MenuItemData(
                                            "Supprimer",
                                            { presetPageViewModel.deleteExercice(exInPreset.idExercise) },
                                            MaterialTheme.typography.bodyMedium,
                                            modeActual.isNormal(),
                                            color = Color.Red.copy(
                                                alpha = if (modeActual.isNormal()) 0.8f else 0.4f
                                            )


                                        )

                                    ),
                                    onExpandChange = { expanded ->
                                        presetPageViewModel.expandExercise(if (expanded) exInPreset.idExercise else null)
                                    },

                                    componentRight = { showMenu ->
                                        if (modeActual.isChangeOrder()) {
                                            Icon(
                                                Icons.Outlined.Reorder,
                                                contentDescription = "Ouvrir le menu",
                                                tint = secondaryTextColor,
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                        } else {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Icon(
                                                    if (state.expandedExerciseId == exInPreset.idExercise) {
                                                        Icons.Outlined.KeyboardArrowUp
                                                    } else {
                                                        Icons.Outlined.KeyboardArrowDown
                                                    },
                                                    "", modifier = Modifier.size(20.dp)
                                                )
                                                Icon(
                                                    Icons.Default.MoreVert,
                                                    contentDescription = "Ouvrir le menu",
                                                    tint = secondaryTextColor,
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clickable(onClick = { showMenu() })
                                                )

                                            }
                                        }
                                    },
                                    canExtend = (!modeActual.isChangeOrder()),
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
                                            mutableStateOf(
                                                TextFieldValue(
                                                    exInPreset.rangeRepSup?.toString().orEmpty()
                                                )
                                            )
                                        }
                                        val list = listOf(
                                            ParameterItem(
                                                label = "Nombre de série",
                                                composableRight = {
                                                    Row(
                                                        modifier.height(40.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        CustomButton(
                                                            "-",
                                                            emphasis = EmphasisType.TERTIA,
                                                            modifier = Modifier.width(40.dp),
                                                            size = SizeType.SMALL,
                                                            enable = (modeActual.isNormal() && (exInPreset.nbSet) > 1),
                                                            onClick = {
                                                                presetPageViewModel.changeNbSet(
                                                                    exInPreset.idExercise,
                                                                    -1
                                                                )
                                                            })
                                                        Text(
                                                            exInPreset.nbSet.toString(),
                                                            modifier = Modifier.padding(horizontal = 20.dp),
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        CustomButton(
                                                            "+",
                                                            emphasis = EmphasisType.TERTIA,
                                                            modifier = Modifier.width(40.dp),
                                                            size = SizeType.SMALL,
                                                            enable = (modeActual.isNormal() && (exInPreset.nbSet) < 20),
                                                            onClick = {
                                                                presetPageViewModel.changeNbSet(
                                                                    exInPreset.idExercise,
                                                                    +1
                                                                )
                                                            })

                                                    }
                                                }),
                                            ParameterItem(
                                                label = "Fourchette de rep",
                                                composableRight = {
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
                                                            enabled = modeActual.isNormal(),
                                                            centerText = true,
                                                            onFocusLost = { value ->
                                                                presetPageViewModel.updateExerciseRange(
                                                                    exerciseId = exInPreset.idExercise,
                                                                    rangeRepInf = value.toIntOrNull(),
                                                                    rangeRepSup = repMaxState.value.text.toIntOrNull()

                                                                )
                                                                val updatedEx =
                                                                    presetPageViewModel.uiState.value.currentPreset?.presetSeanceExerciseVO
                                                                        ?.find { it.idExercise == exInPreset.idExercise }

                                                                repMinState.value = TextFieldValue(
                                                                    (updatedEx?.rangeRepInf
                                                                        ?: "").toString()
                                                                )
                                                                repMaxState.value = TextFieldValue(
                                                                    (updatedEx?.rangeRepSup
                                                                        ?: "").toString()
                                                                )
                                                            }
                                                        )

                                                        Text(
                                                            "à",
                                                            modifier = Modifier.padding(horizontal = 20.dp),
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )

                                                        InputCustom(
                                                            size = SizeType.NO,
                                                            modifier = Modifier.size(40.dp),
                                                            singleLine = true,
                                                            maxCaracter = 2,
                                                            centerText = true,
                                                            enabled = modeActual.isNormal(),
                                                            text = repMaxState,
                                                            keyboardType = KeyboardType.Number,
                                                            onFocusLost = { value ->
                                                                presetPageViewModel.updateExerciseRange(
                                                                    exerciseId = exInPreset.idExercise,
                                                                    rangeRepInf = repMinState.value.text.toIntOrNull(),
                                                                    rangeRepSup = value.toIntOrNull(),
                                                                )
                                                                val updatedEx =
                                                                    presetPageViewModel.uiState.value.currentPreset?.presetSeanceExerciseVO
                                                                        ?.find { it.idExercise == exInPreset.idExercise }

                                                                repMinState.value = TextFieldValue(
                                                                    (updatedEx?.rangeRepInf
                                                                        ?: "").toString()
                                                                )
                                                                repMaxState.value = TextFieldValue(
                                                                    (updatedEx?.rangeRepSup
                                                                        ?: "").toString()
                                                                )
                                                            }
                                                        )
                                                    }
                                                })
                                        )
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(start = 16.dp, end = 16.dp)
                                        ) {
                                            Row(
                                                Modifier
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically

                                            ) {

                                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    ChipDropDown(
                                                        listOfSelectableItem = state.masterDataMaps?.lateraliteMap?.values?.toList()
                                                            ?: emptyList(),
                                                        defaultSelected = state.masterDataMaps?.lateraliteMap[exInPreset.idLateralite]
                                                            ?: "",
                                                        canChange = modeActual.isNormal()

                                                    ) { selectedName ->
                                                        val lateriteId =
                                                            state.masterDataMaps?.lateraliteMap?.entries?.firstOrNull { it.value == selectedName }?.key
                                                                ?: 1L
                                                        presetPageViewModel.updatePresetProperty(
                                                            exInPreset.idExercise,
                                                            ExerciceInPropertyType.LATERALITE,
                                                            lateriteId
                                                        )
                                                    }
                                                    ChipDropDown(
                                                        listOfSelectableItem = state.masterDataMaps?.equipmentMap?.values?.toList()
                                                            ?: emptyList(),
                                                        defaultSelected = state.masterDataMaps?.equipmentMap[exInPreset.idEquipment],
                                                        canChange = modeActual.isNormal()
                                                    ) { selectedName ->
                                                        val equipmentId =
                                                            state.masterDataMaps?.equipmentMap?.entries?.firstOrNull { it.value == selectedName }?.key
                                                                ?: 1L
                                                        presetPageViewModel.updatePresetProperty(
                                                            exInPreset.idExercise,
                                                            ExerciceInPropertyType.EQUIPMENT,
                                                            equipmentId
                                                        )
                                                    }
                                                }


                                            }

                                            ParameterBox(
                                                list,
                                                PaddingValues(
                                                    bottom = 16.dp,
                                                    end = 4.dp,
                                                    start = 4.dp
                                                ),
                                                hasBorder = false
                                            )

                                        }
                                    }
                                )
                            }
                        }


                    }
                }
            }


        }
        if (modeActual.isNormal()) {
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
    BottomSheetInput(
        bottomSheetVisibility = state.isBottomSheetVisible == BottomSheetVisibleType.INPUTPRESET,
        onClose = { presetPageViewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        onNew = { value ->
            presetViewModel.savePreset(state.currentPreset!!.copy(name = value))
            presetPageViewModel.setPreset(state.currentPreset!!.copy(name = value))
            presetPageViewModel.toggleBottomSheet(BottomSheetVisibleType.NO)

        },
        title = "Renommer le Preset",
        buttonText = "Renommer",
        inputDefaultValue = state.currentPreset?.name
    )

    activeMenu?.let { menu ->
        CenterMenu(
            onDismiss = { activeMenu = null },

            title = menu.title,
            descrition = menu.description,
            items = menu.content


        )
    }

}





