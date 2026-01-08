package com.dyentrack.app.presentation.screens.seance

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ActiveMenu
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.domain.model.enum.ExerciseMode
import com.dyentrack.app.domain.model.enum.SaveType
import com.dyentrack.app.domain.model.enum.SectionSelectType
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.components.AdaptiveAdBanner
import com.dyentrack.app.presentation.components.BottomSheetPreset
import com.dyentrack.app.presentation.components.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.components.CenterMenu
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.globalViewModel.AdViewModel
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.globalViewModel.HealthConnectViewModel
import com.dyentrack.app.presentation.globalViewModel.InfoUserViewModel
import com.dyentrack.app.presentation.globalViewModel.PresetViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.screens.seance.component.DateSelectorRange
import com.dyentrack.app.presentation.screens.seance.dataSection.DataSection
import com.dyentrack.app.presentation.screens.seance.seanceSection.component.ExerciseCard
import com.dyentrack.app.presentation.screens.seance.seanceSection.component.ShowInfoOfSeance
import com.dyentrack.app.presentation.theme.secondaryTextColor
import kotlin.math.roundToInt


@Composable

fun SeancePage(
    modifier: Modifier = Modifier,
    viewModel: SeanceViewModel = hiltViewModel<SeanceViewModel>(),
    navController: NavController,
    daySelected: String? = null,
    section: String? = null,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    healthViewModel: HealthConnectViewModel = hiltViewModel<HealthConnectViewModel>(),
    adViewModel: AdViewModel = hiltViewModel<AdViewModel>(),
    infoUserViewModel: InfoUserViewModel = hiltViewModel<InfoUserViewModel>(),
    presetViewModel: PresetViewModel = hiltViewModel<PresetViewModel>(),
) {
    LaunchedEffect(Unit) {
        daySelected?.let { viewModel.setDaySelected(it) }
        section?.let { viewModel.setSectionSelected(it) }
    }

    val userPreference by userPreferenceViewModel.userPreference.collectAsState()


    val state by viewModel.uiState.collectAsState()
    val infoMapUser by infoUserViewModel.infoExerciseUserFlow.collectAsState()
    val authViewModel by authViewModel.authState.collectAsState()
    val language = viewModel.languageUser
    val masterData by viewModel.masterData.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val userUnit = userPreference?.mesureUnit ?: MesureUnit.KG
    val textToast = stringResource(R.string.favorite_login_prompt)
    val modeActual = state.exerciseMode
    val todaySeance = state.seancesParJour[state.daySelected]

    var activeMenu by remember { mutableStateOf<ActiveMenu?>(null) }
    val presets by presetViewModel.presetFlow.collectAsState()



    val revokePresetTitle = stringResource(R.string.revoke_preset_title)
    val revokePresetDescription = stringResource(R.string.revoke_preset_description)

    val addPresetExercisesTitle = stringResource(R.string.add_preset_exercises_title)
    val addPresetExercisesDescription = stringResource(R.string.add_preset_exercises_description)

    val yesText = stringResource(R.string.yes)
    val noText = stringResource(R.string.no)
    val associateOnlyText = stringResource(R.string.associate_only)

    val associatePresetText = stringResource(R.string.associate_preset)
    val importFromPresetText = stringResource(R.string.import_from_preset)

    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) {
        Column {
            TopNavigationBar(
                stringResource(R.string.today_session),
                rightSide = {
                    if (todaySeance?.saveAPI == SaveType.NOTSAVE) { //TODO( "add Flow of requestDayDataStatus")
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                })


            LazyColumn {
                item { DateSelectorRange(uiState = state, viewModel = viewModel) }
                item {
                    AdaptiveAdBanner(
                        adViewModel = adViewModel

                    )
                }
                item {
                    Box(modifier = Modifier.padding(top = 14.dp)) {
                        SectionSelector(
                            variant = VariantType.OUTLINED,
                            itemSection = listOf(
                                MenuItemData(
                                    stringResource(R.string.sessions),
                                    { viewModel.changeSection(SectionSelectType.SEANCE) }
                                ),
                                MenuItemData(
                                    stringResource(R.string.data),
                                    { viewModel.changeSection(SectionSelectType.DATA) }
                                )
                            ),
                            selectedIndex = if (state.sectionSelect == SectionSelectType.SEANCE) 0 else 1


                        )
                    }
                }

                when (state.sectionSelect) {
                    SectionSelectType.SEANCE -> {
                        val exercises =
                            todaySeance?.exercisesInSceance ?: listOf()

                        if (exercises.isNotEmpty()) {
                            item {
                                Row(
                                    modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box {
                                        if (modeActual.isNormal()) {
                                            if (todaySeance?.idPreset?.localId == null && todaySeance?.idPreset?.apiId == null) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .clickable(onClick = {
                                                            viewModel.toggleBottomSheet(
                                                                BottomSheetVisibleType.ONLYPRESET
                                                            )
                                                        })
                                                ) {
                                                    Icon(
                                                        Icons.Default.AddLink,
                                                        stringResource(R.string.associate_preset),
                                                        tint = MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.6f
                                                        ),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        stringResource(R.string.associate_preset),
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = MaterialTheme.colorScheme.primary.copy(
                                                                alpha = 0.6f
                                                            )
                                                        )
                                                    )
                                                }
                                            } else {
                                                val currentPreset =
                                                    presets.find { it.idLocal == todaySeance.idPreset.localId || todaySeance.idPreset.apiId == it.idPreset }
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 4.dp).clickable(
                                                        onClick = {


                                                            activeMenu = ActiveMenu(
                                                                title = revokePresetTitle,
                                                                description = revokePresetDescription,
                                                                content = listOf(
                                                                    MenuItemData(noText, onClick = { activeMenu = null }),
                                                                    MenuItemData(yesText, color = Color.Red, onClick = {
                                                                        viewModel.setPreset(null, null)
                                                                        activeMenu = null

                                                                    }
                                                                    )
                                                                )
                                                            )
                                                        }
                                                    )
                                                ) {
                                                    Icon(
                                                        Icons.Default.Link,
                                                        "",
                                                        tint = MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.6f
                                                        ),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        currentPreset?.name ?: stringResource(R.string.old_preset),
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = MaterialTheme.colorScheme.primary.copy(
                                                                alpha = 0.6f
                                                            )
                                                        )
                                                    )
                                                }

                                            }
                                        }
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(
                                            12.dp,
                                            Alignment.End
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        IconButton(
                                            onClick = {
                                                viewModel.toggleMode(ExerciseMode.LOCK)
                                            }
                                        ) {
                                            Icon(
                                                Icons.Outlined.Lock,
                                                contentDescription = "Lock Mode",
                                                tint = if (modeActual.isLock()) MaterialTheme.colorScheme.primary else secondaryTextColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.toggleMode(ExerciseMode.CHANGE_ORDER)
                                            },
                                        ) {
                                            Icon(
                                                Icons.Outlined.SwapVert,
                                                contentDescription = "Swap Mode",
                                                tint = if (modeActual.isChangeOrder()) MaterialTheme.colorScheme.primary else secondaryTextColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            itemsIndexed(
                                exercises,
                                key = { _, ex -> ex.exerciseId }) { _, exInSeance ->
                                var offsetY by remember { mutableFloatStateOf(0f) }

                                Box(
                                    modifier = Modifier
                                        .padding(10.dp, 4.dp)
                                        .offset { IntOffset(0, offsetY.roundToInt()) }
                                        .then(
                                            if (state.exerciseMode == ExerciseMode.CHANGE_ORDER) {
                                                Modifier.pointerInput(exInSeance) {
                                                    detectDragGestures(
                                                        onDragStart = { },
                                                        onDrag = { change, dragAmount ->
                                                            offsetY += dragAmount.y
                                                            change.consume()
                                                            val threshold = 70.dp.toPx()
                                                            val lastIndex = exercises.lastIndex


                                                            val currentIndex =
                                                                exercises.indexOf(exInSeance)

                                                            if (offsetY > threshold && currentIndex < lastIndex) {
                                                                viewModel.swapExercise(
                                                                    state.daySelected,
                                                                    currentIndex,
                                                                    currentIndex + 1
                                                                )
                                                                offsetY = 0f
                                                            } else if (offsetY < -threshold && currentIndex > 0) {
                                                                viewModel.swapExercise(
                                                                    state.daySelected,
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
                                    ExerciseCard(
                                        exInSeance,
                                        state,
                                        viewModel,
                                        navController,
                                        userUnit,

                                        )
                                }
                            }
                            item {
                                val exerciseSummary by remember(state.daySelected) {
                                    mutableStateOf(viewModel.getSeanceSummary(state.daySelected))
                                }
                                ShowInfoOfSeance(exerciseSummary, userUnit) }


                        } else {
                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        stringResource(R.string.no_workout),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        stringResource(R.string.add_workout),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )

                                }
                            }
                        }
                    }

                    SectionSelectType.DATA ->
                        item {
                            DataSection(
                                viewModel,
                                state.exerciseMode == ExerciseMode.LOCK,
                                state.daySelected,
                                state,
                                healthViewModel,
                                userPreference?.autoSync ?: true,
                                userUnit
                            )
                        }

                    else -> null
                }

            }


        }

        if (state.exerciseMode != ExerciseMode.LOCK && state.sectionSelect == SectionSelectType.SEANCE) {
            FloatingActionButton(

                onClick = {
                    viewModel.setExerciseToAdd(
                        todaySeance?.exercisesInSceance
                            ?.map { it.exerciseId }
                            ?.toList()
                            ?: listOf()
                    )
                    viewModel.toggleBottomSheet(BottomSheetVisibleType.ONLYEXERCICE)
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







    BottomSheetPreset(
        bottomSheetVisibility = state.isBottomSheetVisible == BottomSheetVisibleType.ONLYPRESET,
        onClose = { viewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        onNew = { viewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        title = if(todaySeance?.exercisesInSceance?.isEmpty()==true) importFromPresetText else associatePresetText,
        presetList = presets,
        titleAction = {
        if(todaySeance?.exercisesInSceance?.isEmpty()==true) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {  viewModel.toggleBottomSheet(BottomSheetVisibleType.ONLYEXERCICE) },
                    tint = MaterialTheme.colorScheme.onSurface
                )

        }
                      },
        onClickPreset = { preset ->
            viewModel.setPreset(preset.idLocal, preset.idPreset)

            viewModel.toggleBottomSheet(BottomSheetVisibleType.NO)
            if(todaySeance?.exercisesInSceance?.isEmpty()==true) {
                viewModel.addExerciseFromPresetForDaySelected(presets);
            } else {
                activeMenu = ActiveMenu(
                    title =addPresetExercisesTitle,
                    description = addPresetExercisesDescription,
                    content = listOf(
                        MenuItemData(associateOnlyText, onClick = { activeMenu = null }),
                        MenuItemData(yesText, onClick = {
                            viewModel.addExerciseFromPresetForDaySelected(presets);
                            activeMenu = null
                        })
                    )
                )
            }
        }
    )





    BottomSheetSelectionExercise(
        bottomSheetVisibility = state.isBottomSheetVisible == BottomSheetVisibleType.ONLYEXERCICE,
        selectedExerciseIds = state.exerciseToAdd,
        navController = navController,
        title = stringResource(R.string.add_exercise),
        titleAction = {
            if(todaySeance?.exercisesInSceance?.isEmpty()==true) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Menu",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { viewModel.toggleBottomSheet(BottomSheetVisibleType.ONLYPRESET)},
                        tint = MaterialTheme.colorScheme.onSurface
                    )
            }
        },
        onClose = { viewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        onClickExercise = { exercise ->
            viewModel.toggleExerciseSelection(exercise.idExercise)
        },
        officialExercise = masterData?.officialExercise,
        infoMapUser = infoMapUser,
        onClickFavorite = {
            if (authViewModel.isConnected) infoUserViewModel.updateInfoExerciseUser(
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
                onClick = { viewModel.toggleBottomSheet(BottomSheetVisibleType.NO) }
            )
            CustomButton(
                label = stringResource(R.string.add),
                modifier = Modifier.weight(1f),
                onClick = { viewModel.addExerciseToSeance() }
            )
        }

    }


    activeMenu?.let { menu ->
        CenterMenu(
            onDismiss = { activeMenu = null },

            title = menu.title,
            descrition = menu.description,
            items = menu.content


        )
    }
}
