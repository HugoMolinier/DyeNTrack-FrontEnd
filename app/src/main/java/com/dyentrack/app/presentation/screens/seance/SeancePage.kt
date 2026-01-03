package com.dyentrack.app.presentation.screens.seance

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.presentation.components.AdaptiveAdBanner
import com.dyentrack.app.presentation.components.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.domain.model.enum.SectionSelectType
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.globalViewModel.AdViewModel
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.globalViewModel.HealthConnectViewModel
import com.dyentrack.app.presentation.globalViewModel.InfoUserViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.screens.seance.component.DateSelectorRange
import com.dyentrack.app.presentation.screens.seance.component.ModeMenu
import com.dyentrack.app.presentation.screens.seance.dataSection.DataSection
import com.dyentrack.app.presentation.screens.seance.seanceSection.component.ExerciseCard
import com.dyentrack.app.presentation.screens.seance.seanceSection.component.ShowInfoOfSeance
import com.dyentrack.app.presentation.screens.seance.viewModel.ExerciseMode
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import kotlin.math.roundToInt

@Composable

fun SeancePage(
    modifier: Modifier = Modifier,
    viewModel: SeanceViewModel = hiltViewModel<SeanceViewModel>(),
    navController: NavController,
    daySelected: String?=null,
    section:String?=null,
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
    authViewModel: AuthViewModel= hiltViewModel<AuthViewModel>(),
    healthViewModel: HealthConnectViewModel =hiltViewModel<HealthConnectViewModel>(),
    adViewModel: AdViewModel=hiltViewModel<AdViewModel>(),
    infoUserViewModel: InfoUserViewModel=hiltViewModel<InfoUserViewModel>(),
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

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val userUnit =userPreference?.mesureUnit?: MesureUnit.KG
val textToast = stringResource(R.string.favorite_login_prompt)

    Box(modifier = modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })}
    ) {
        Column {
            TopNavigationBar(
                stringResource(R.string.today_session), leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("home") },
                rightSide = {
                    if (viewModel.dayDataFlow.collectAsState().value == null) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {


                        ModeMenu(
                            currentMode = state.exerciseMode,
                            onToggleMode = { mode ->
                                viewModel.toggleMode(mode)
                            }
                        )
                    }
                })


            LazyColumn {
                item { DateSelectorRange(uiState = state, viewModel = viewModel) }
                item{
                    AdaptiveAdBanner(adViewModel=adViewModel

                    )
                }
                item {
                    Box (modifier= Modifier.padding(top = 14.dp, bottom = 20.dp)) {
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
                            state.seancesParJour[state.daySelected]?.exercisesInSceance ?: listOf()

                        if (exercises.isNotEmpty()) {
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
                            item { ShowInfoOfSeance(viewModel,userUnit) }


                        } else {
                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 20.dp),
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
                            DataSection(viewModel,    state.exerciseMode == ExerciseMode.LOCK,state.daySelected,state,healthViewModel,userPreference?.autoSync?:true,userUnit)
                        }

                    else -> null
                }

            }


        }

        if (state.exerciseMode != ExerciseMode.LOCK && state.sectionSelect == SectionSelectType.SEANCE) {
            FloatingActionButton(

                onClick = {
                    viewModel.setExerciseToAdd(
                        state.seancesParJour[state.daySelected]?.exercisesInSceance
                            ?.map { it.exerciseId }
                            ?.toList()
                            ?: listOf()
                    )
                    viewModel.toggleBottomSheet(true)
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
        bottomSheetVisibility = state.isBottomSheetVisible,
        selectedExerciseIds = state.exerciseToAdd,
        navController = navController,
        title=stringResource(R.string.add_exercise),
        onClose = { viewModel.toggleBottomSheet(false) },
        onClickExercise = { exercise ->
            viewModel.toggleExerciseSelection(exercise.idExercise)
        },
        officialExercise = viewModel.masterData?.officialExercise,
        infoMapUser = infoMapUser,
        onClickFavorite = { if(authViewModel.isConnected) infoUserViewModel.updateInfoExerciseUser(it, !(infoMapUser[it]?.favorite ?: false), null) else {
            Toast.makeText(context, textToast, Toast.LENGTH_SHORT).show()
        } },
        userlanguage= language,
        musclesGroupMap=state.masterDataMaps?.musclesGroupMap
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
                onClick = { viewModel.toggleBottomSheet(false) }
            )
            CustomButton(
                label = stringResource(R.string.add),
                modifier = Modifier.weight(1f),
                onClick = { viewModel.addExerciseToSeance() }
            )
        }
    }
}