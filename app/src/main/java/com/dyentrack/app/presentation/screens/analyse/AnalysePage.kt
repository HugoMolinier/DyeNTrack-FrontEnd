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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.presentation.components.bottomSheet.BottomSheetPreset
import com.dyentrack.app.presentation.components.bottomSheet.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.components.ChipDropDownFlowRow
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.ProgressLine
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.chart.CombinedChartMP
import com.dyentrack.app.presentation.components.chart.PieChartMP
import com.dyentrack.app.presentation.components.chart.RadarChartMP
import com.dyentrack.app.presentation.globalViewModel.InfoUserViewModel
import com.dyentrack.app.presentation.globalViewModel.PresetViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.theme.BlueExercise
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.convertWeight
import com.dyentrack.app.presentation.utils.toAbbreviatedDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Composable
fun AnalysePage(
    modifier: Modifier = Modifier, navController: NavController,
    analyseViewModel: AnalyseViewModel = hiltViewModel<AnalyseViewModel>(),
    presetViewModel: PresetViewModel = hiltViewModel<PresetViewModel>(),
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
    infoUserViewModel: InfoUserViewModel = hiltViewModel<InfoUserViewModel>(),
) {
    val state by analyseViewModel.uiState.collectAsState()
    val userPref by userPreferenceViewModel.userPreference.collectAsState()
    val allPreset by presetViewModel.presetFlow.collectAsState()
    LaunchedEffect(allPreset) {
        if (allPreset.isNotEmpty())
            analyseViewModel.choseThisPreset(allPreset.firstOrNull())
    }
    val infoMapUser by infoUserViewModel.infoExerciseUserFlow.collectAsState()
    var inputIndex by remember { mutableIntStateOf(0) }
    var extendFilter by remember { mutableStateOf(false) }
    val userUnit by remember(userPref) { mutableStateOf(userPref?.mesureUnit ?: MesureUnit.KG) }

    val userLANGUAGE by remember(analyseViewModel.languageUser) { mutableStateOf(analyseViewModel.languageUser) }

    val filteredData by remember(state.dateRange, state.notIncludeGroupMuscle) {
        mutableStateOf(
            analyseViewModel.getFilteredMuscleRateByDay(
                state.dateRange.oldDate, state.dateRange.recentDate,
                state.notIncludeGroupMuscle
            )
        )
    }
    if (state.exerciceChose == 0L) {
        analyseViewModel.choseThisExercise(filteredData.values.first().perExerciseStat.keys.first())
    }

    val masterData by analyseViewModel.masterData.collectAsState()

    val globalMuscleRate by remember(filteredData) {
        mutableStateOf(analyseViewModel.computeGlobalMuscleRate(filteredData))
    }

    val moyenneSetParEx = analyseViewModel.moyenneSetParEx(globalMuscleRate)
    val proportionSetFail = analyseViewModel.proportionSetFail(globalMuscleRate)
    val sommePondUserUnit =
        convertWeight(globalMuscleRate.rateSetSomme?.sommePond, MesureUnit.KG, userUnit)
    Column(modifier = modifier.fillMaxWidth()) {
        TopNavigationBar(stringResource(R.string.full_analysis))
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
                                ChipDropDownFlowRow(
                                    state.masterDataMaps!!.musclesGroupMap,
                                    state.notIncludeGroupMuscle,
                                    onClick = { analyseViewModel.changeNotIncludeList(it) })
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
                                Text(filteredData.count { it.value.perExerciseStat.isNotEmpty() }
                                    .toString(), style = MaterialTheme.typography.bodyLarge)

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
                    val allStatByDay: Map<String, MuscleRate> =
                        filteredData.mapValues { it.value.AllStat }
                    val chartData = analyseViewModel.getCombinedChartData(allStatByDay, userUnit)

                    CombinedChartMP(
                        labels = chartData.labels,
                        barValues = chartData.barValues,
                        lineValues = chartData.lineValues,
                        showValueBar = chartData.showValueBar,
                        showValueLine = chartData.showValueLine,
                        modifier = Modifier.fillMaxWidth(),
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
                            val radarData = remember(filteredData, state.masterDataMaps) {
                                analyseViewModel.getRadarData(
                                    data = filteredData.mapValues { it.value.perGroupMuscularStat }, // <-- ici on prend directement perMuscleStat
                                    labelsMap = state.masterDataMaps?.musclesGroupMap ?: emptyMap()
                                )
                            }
                            RadarChartMP(
                                modifier = Modifier.fillMaxSize(),
                                radarData.first,
                                radarData.second,
                                radarData.third
                            )

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
                            val radarData = remember(filteredData, state.masterDataMaps) {
                                analyseViewModel.getRadarData(
                                    data = filteredData.mapValues { it.value.perMuscleStat }, // <-- ici on prend directement perMuscleStat
                                    labelsMap = state.masterDataMaps?.muscleMap?.mapValues {
                                        it.value.getName(userLANGUAGE)
                                    } ?: emptyMap()
                                )
                            }
                            RadarChartMP(
                                modifier = modifier,
                                radarData.first,
                                radarData.second,
                                radarData.third
                            )
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
                    val musclePercentage =
                        analyseViewModel.calculateMuscleSetPercentage(filteredData)
                    musclePercentage.forEach { (key, value) ->
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
            if (allPreset.isNotEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(
                            "Par Preset",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding( bottom = 8.dp)
                        )

                    }
                }

                item {

                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth()
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
                                text = (state.presetChose?.name ?: "No name Preset"),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = {
                                analyseViewModel.toggleBottomSheet(
                                    BottomSheetVisibleType.ONLYPRESET
                                )
                            }) {
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = stringResource(R.string.change_selection),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        val presetData = state.presetChose?.let { preset ->
                            filteredData
                                .filter { it.value.presetId?.localId == preset.idLocal || it.value.presetId?.apiId == preset.idPreset }
                                .mapValues { it.value.AllStat }
                        } ?: emptyMap()

                        val chartDataPreset = analyseViewModel.getCombinedChartData(
                            data = presetData,
                            userUnit = userUnit
                        )
                        CombinedChartMP(
                            labels = chartDataPreset.labels,
                            barValues = chartDataPreset.barValues,
                            lineValues = chartDataPreset.lineValues,
                            showValueBar = chartDataPreset.showValueBar,
                            showValueLine = chartDataPreset.showValueLine,
                            modifier = Modifier
                                .fillMaxWidth(),
                            barLegendLabel = stringResource(R.string.weighted_sum),
                            lineLegendLabel = stringResource(R.string.fail_set_ratio)
                        )
                    }

                }
            }
            item {

                Row(modifier=Modifier.fillMaxWidth().padding(top=12.dp,bottom=12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val presetUsage = remember(filteredData, allPreset) {
                        val usageList = mutableListOf<Triple<String, Int, Float>>() // name, count, ratio%
                        var usedCount = 0
                        val total = filteredData.size.coerceAtLeast(1) // éviter div par 0

                        // Presets existants
                        allPreset.forEach { preset ->
                            val count = filteredData.count {
                                it.value.presetId?.localId == preset.idLocal ||
                                        it.value.presetId?.apiId == preset.idPreset
                            }
                            if (count > 0) {
                                usedCount += count
                                usageList.add(Triple(preset.name, count, count.toFloat() / total * 100f))
                            }
                        }

                        // No preset
                        val noPresetCount = total - usedCount
                        if (noPresetCount > 0) {
                            usageList.add(Triple("No preset", noPresetCount, noPresetCount.toFloat() / total * 100f))
                        }

                        usageList
                    }
                    val pieLabels = presetUsage.map { it.first }
                    val pieValues = presetUsage.map { it.third }
                    val baseColor = MaterialTheme.colorScheme.primary
                    val pieColors = pieLabels.mapIndexed { index, _ ->

                        val hsv = FloatArray(3)
                        android.graphics.Color.colorToHSV(baseColor.toArgb(), hsv)
                        hsv[0] = (hsv[0] + index * 45f) % 360f
                        Color(android.graphics.Color.HSVToColor((0.5f * 255).toInt(), hsv))
                    }

                    PieChartMP(
                        modifier = Modifier.fillMaxWidth(0.6f) .aspectRatio(1f),
                        labels = pieLabels,
                        values = pieValues,
                        colors = pieColors
                    )
                    Column(modifier = Modifier.padding(top = 8.dp,start=8.dp)) {

                        Text(
                            text = ("Nombre d'occurence"),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier=Modifier.padding(bottom=12.dp)
                        )
                        presetUsage.forEach { (name, count, _) ->


                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    name,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    "$count",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = secondaryTextColor
                                    )
                                )
                            }

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
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding( bottom = 8.dp)
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
                        IconButton(onClick = {
                            analyseViewModel.toggleBottomSheet(
                                BottomSheetVisibleType.ONLYEXERCICE
                            )
                        }) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = stringResource(R.string.change_selection),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Construire les données par date pour le graphique
                    val allDataExercise = filteredData
                        .mapNotNull { (day, dayData) ->
                            dayData.perExerciseStat[state.exerciceChose]
                                ?.let { day to it }
                        }.toMap()

                    val chartDataExercise = analyseViewModel.getCombinedChartData(
                        data = allDataExercise,
                        userUnit = userUnit
                    )

                    CombinedChartMP(
                        labels = chartDataExercise.labels,
                        barValues = chartDataExercise.barValues,
                        lineValues = chartDataExercise.lineValues,
                        showValueBar = chartDataExercise.showValueBar,
                        showValueLine = chartDataExercise.showValueLine,
                        modifier = Modifier.fillMaxWidth(),
                        barLegendColor = BlueExercise.copy(alpha = 0.8f),
                        barLegendLabel = stringResource(R.string.weighted_sum),
                        lineLegendLabel = stringResource(R.string.fail_set_ratio)
                    )
                }

            }

        }
    }
    BottomSheetPreset(
        bottomSheetVisibility = state.isBottomSheetVisible == BottomSheetVisibleType.ONLYPRESET,
        onClose = { analyseViewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        onNew = { analyseViewModel.toggleBottomSheet(BottomSheetVisibleType.INPUTPRESET) },
        title = "Preset",
        presetList = allPreset,
        onClickPreset = { preset ->
            analyseViewModel.choseThisPreset(preset)
            analyseViewModel.toggleBottomSheet(BottomSheetVisibleType.NO)
        },
        canAddPreset = false

    )
    BottomSheetSelectionExercise(
        bottomSheetVisibility = state.isBottomSheetVisible == BottomSheetVisibleType.ONLYEXERCICE,
        selectedExerciseIds = listOf(state.exerciceChose),
        officialExercise = masterData?.officialExercise,
        infoMapUser = infoMapUser,
        title = stringResource(R.string.choose_exercise),
        navController = navController,
        onClose = { analyseViewModel.toggleBottomSheet(BottomSheetVisibleType.NO) },
        onClickExercise = { exercise ->
            analyseViewModel.choseThisExercise(exercise.idExercise);analyseViewModel.toggleBottomSheet(
            BottomSheetVisibleType.NO
        )
        },
        onClickFavorite = {
            infoUserViewModel.updateInfoExerciseUser(
                it,
                !(infoMapUser[it]?.favorite ?: false),
                null
            )
        },
        userlanguage = userLANGUAGE,
        musclesGroupMap = state.masterDataMaps?.musclesGroupMap
    )


}

