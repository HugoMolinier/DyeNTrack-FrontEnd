package com.dyentrack.app.presentation.screens.analyse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.SettingsInputComponent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.presentation.components.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.components.ChipDropDownFlowRow
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.ProgressLine
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.chart.CombinedChartMP
import com.dyentrack.app.presentation.components.chart.RadarChartMP
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.screens.analyse.viewModel.AnalyseViewModel
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.convertWeight
import com.dyentrack.app.presentation.utils.toAbbreviatedDate


@Composable
fun AnalysePage(
    modifier: Modifier = Modifier, navController: NavController,
    analyseViewModel: AnalyseViewModel = hiltViewModel<AnalyseViewModel>(),
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
) {
    val state by analyseViewModel.uiState.collectAsState()
    val userPref by userPreferenceViewModel.userPreference.collectAsState()

    val infoMapUser by analyseViewModel.infoExerciseUserFlow.collectAsState()
    var inputIndex by remember { mutableIntStateOf(0) }
    var extendFilter by remember { mutableStateOf(false) }
    val userUnit by remember(userPref) { mutableStateOf(userPref?.mesureUnit ?: MesureUnit.KG) }

    val userLANGUAGE by remember(analyseViewModel.languageUser) { mutableStateOf(analyseViewModel.languageUser) }
    val globalData by remember(state.muscleRateOfEachDay) {
        mutableStateOf(analyseViewModel.computeGlobalMuscleRate())
    }
    val moyenneSetParEx by remember(globalData) {
        mutableStateOf(analyseViewModel.moyenneSetParEx(globalData))
    }
    val proportionSetFail by remember(globalData) {
        mutableStateOf(analyseViewModel.proportionSetFail(globalData))
    }
    val sommePondUserUnit by remember(globalData, userUnit) {
        mutableStateOf(convertWeight(globalData.rateSetSomme?.sommePond, MesureUnit.KG, userUnit))
    }
    Column(modifier = modifier.fillMaxWidth()) {
        TopNavigationBar(
            stringResource(R.string.full_analysis), leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onClickLeft = { navController.popBackStack() })
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 20.dp)
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SectionSelector(
                            modifier = Modifier,
                            selectedIndex = inputIndex,
                            itemSection = listOf(
                                MenuItemData(
                                    stringResource(R.string.two_weeks),
                                    { analyseViewModel.applyDateRange(14L);inputIndex = 0 }
                                ),
                                MenuItemData(
                                    stringResource(R.string.month),
                                    { analyseViewModel.applyDateRange(30L);inputIndex = 1 }
                                ),
                                MenuItemData(
                                    stringResource(R.string.all),
                                    { analyseViewModel.applyDateRange(3000L);inputIndex = 2 }
                                )
                            ))
                    }
                    val isSelected = inputIndex == 3
                    Box(
                        modifier = Modifier
                            .background(
                                TertiaOne,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(
                                enabled = !isSelected,
                                onClick = { inputIndex = 3 }
                            )
                            .padding(2.dp)
                            .background(
                                if (isSelected)
                                    MaterialTheme.colorScheme.background
                                else
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.SettingsInputComponent,
                            contentDescription = stringResource(R.string.custom_section),
                            modifier = Modifier
                                .size(18.dp)
                                .padding(2.dp),
                            tint = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )

                    }
                }

            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        state.dateRange.oldDate.toAbbreviatedDate(userLANGUAGE) +
                                if (state.dateRange.oldDate.year != state.dateRange.recentDate.year) {
                                    " ${state.dateRange.oldDate.year} - "
                                } else {
                                    " - "
                                } +
                                state.dateRange.recentDate.toAbbreviatedDate(userLANGUAGE) +
                                " ${state.dateRange.recentDate.year}",
                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(top = 36.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.performance_indicator),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = { extendFilter = !extendFilter },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.FilterAlt,
                                stringResource(R.string.show_filters),
                                tint = if (extendFilter) MaterialTheme.colorScheme.onSurface else secondaryTextColor
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = extendFilter,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.filter_by_muscle_group),
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )

                            if (state.masterDataMaps?.musclesGroupMap?.isNotEmpty() == true) {
                                ChipDropDownFlowRow(state.masterDataMaps!!.musclesGroupMap)
                            }
                        }
                    }
                    Column {
                        //debut composant
                        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(
                                Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    stringResource(R.string.number_of_sessions),
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                                Text(state.mapSeance.filter { it.value.plannedExercises.isNotEmpty() }
                                    .count().toString(), style = MaterialTheme.typography.bodyLarge)

                            }

                        }
                        HorizontalDivider()

                        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(
                                Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    stringResource(R.string.avg_set_per_exercise),
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                                Text(
                                    moyenneSetParEx.toString(),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                            }

                        }
                        HorizontalDivider()
                        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(
                                Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    stringResource(R.string.fail_set_ratio),
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                                Text(
                                    String.format("%.2f%%", proportionSetFail),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                            }

                        }
                        HorizontalDivider()
                        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(
                                Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.weighted_sum),
                                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                    )
                                    Icon(
                                        Icons.Default.Info,
                                        "voir info",
                                        tint = secondaryTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    sommePondUserUnit?.toInt().toString() + userUnit.toString()
                                        .lowercase(),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                            }


                        }
                    }

                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(vertical = 8.dp)


                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    val sizeDayRatio = state.dayRatio.values.size
                    CombinedChartMP(
                        labels = state.dayRatio.keys.toList(),
                        barValues = state.dayRatio.values.map {
                            convertWeight(
                                it.rateSetSomme?.sommePond,
                                MesureUnit.KG,
                                userUnit
                            )?.toFloat() ?: 0f
                        },
                        lineValues = state.dayRatio.values.map {
                            val totalSet = it.numberSet
                            val failSet = it.rateSetSomme?.numberSetFail ?: 0
                            if (totalSet > 0) failSet.toFloat() / totalSet else 0f
                        },
                        showValueBar = sizeDayRatio < 35,
                        showValueLine = sizeDayRatio < 25,
                        modifier = Modifier
                            .fillMaxWidth(),
                        barLegendLabel = stringResource(R.string.weighted_sum),
                        lineLegendLabel = stringResource(R.string.fail_set_ratio)

                    )
                }
            }
            item {
                Text(
                    stringResource(R.string.distribution),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {
                        Box(
                            modifier = Modifier
                                .width(300.dp)
                                .height(328.dp)


                                .wrapContentHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            val radarData = remember(state.graphData, state.masterDataMaps?.musclesGroupMap) {
                                val radarLabels = state.graphData.keys.map { state.masterDataMaps?.musclesGroupMap?.get(it) ?: "N/A" }
                                val values2 = state.graphData.values.map { it.numberExercise.toFloat() }
                                val values3 = state.graphData.values.map { it.numberSet.toFloat() }
                                Triple(radarLabels, values2, values3)
                            }

                            RadarChartMP(modifier=  Modifier.fillMaxSize(),radarData.first,radarData.second,radarData.third)

                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .width(300.dp)
                                .height(328.dp)


                                .wrapContentHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            val radarLabels by remember(
                                state.muscleSetPercentage,
                                state.masterDataMaps
                            ) {
                                mutableStateOf(
                                    state.muscleSetPercentage.mapNotNull { (muscleId, _, _) ->
                                        state.masterDataMaps?.muscleMap?.get(muscleId)
                                            ?.getName(userLANGUAGE)
                                    }
                                )
                            }
                            val radarValues by remember(state.muscleSetPercentage) {
                                mutableStateOf(state.muscleSetPercentage.map { it.third.numberSet.toFloat() })
                            }
                            val radarValues2 by remember(state.muscleSetPercentage) {
                                mutableStateOf(state.muscleSetPercentage.map { it.third.numberExercise.toFloat() })
                            }
                            RadarChartMP(modifier, radarLabels, radarValues2, radarValues)
                        }
                    }
                }
            }

            item {
                FlowRow(
                    maxItemsInEachRow = 2,
                    modifier = Modifier.padding(vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.muscleSetPercentage.forEach { (key, value) ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth(0.48f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    state.masterDataMaps?.muscleMap?.get(key)?.getName(userLANGUAGE)
                                        ?: "N/A",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    String.format("%.2f%%", value * 100),
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                            }
                            ProgressLine(value, height = 4.dp)
                        }
                    }
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Text(
                        stringResource(R.string.per_exercise),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(vertical = 8.dp)


                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                            .fillMaxWidth()
                            .height(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.masterDataMaps?.exerciseMap?.get(state.exerciceChose)
                                ?.getName(userLANGUAGE)
                                ?: stringResource(R.string.no_exercise),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { analyseViewModel.toggleBottomSheet(true) }) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = stringResource(R.string.change_selection),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    val numberSize = state.allDataExercise.values.size
                    CombinedChartMP(
                        labels = state.allDataExercise.keys.toList(),
                        barValues = state.allDataExercise.values.map {
                            convertWeight(
                                it.rateSetSomme?.sommePond,
                                MesureUnit.KG,
                                userUnit
                            )?.toFloat() ?: 0f
                        },
                        lineValues = state.allDataExercise.values.map {
                            val totalSet = it.numberSet
                            val failSet = it.rateSetSomme?.numberSetFail ?: 0
                            if (totalSet > 0) failSet.toFloat() / totalSet else 0f
                        },
                        showValueBar = numberSize < 35,
                        showValueLine = numberSize < 20,
                        modifier = Modifier
                            .fillMaxWidth(),
                        barLegendLabel = stringResource(R.string.weighted_sum),
                        lineLegendLabel = stringResource(R.string.fail_set_ratio)
                    )
                }
                BottomSheetSelectionExercise(
                    bottomSheetVisibility = state.isBottomSheetVisible,
                    selectedExerciseIds = listOf(state.tempChose),
                    officialExercise = analyseViewModel.masterData?.officialExercise,
                    infoMapUser = infoMapUser,
                    title = stringResource(R.string.choose_exercise),
                    navController = navController,
                    onClose = { analyseViewModel.toggleBottomSheet(false) },
                    onClickExercise = { exerciseId -> analyseViewModel.changeTempExercice(exerciseId) },
                    onClickFavorite = {
                        analyseViewModel.updateInfoExerciseUser(
                            it,
                            !(infoMapUser[it]?.favorite ?: false),
                            null
                        )
                    },
                    userlanguage = userLANGUAGE,
                    musclesGroupMap = state.masterDataMaps?.musclesGroupMap
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CustomButton(
                            label = stringResource(R.string.choose),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                analyseViewModel.choseThisExercise();analyseViewModel.toggleBottomSheet(
                                false
                            )
                            }
                        )
                    }
                }

            }

        }
    }


}
