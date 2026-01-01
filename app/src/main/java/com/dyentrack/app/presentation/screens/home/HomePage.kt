package com.dyentrack.app.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.ProgressLine
import com.dyentrack.app.presentation.components.chart.RadarChartMP
import com.dyentrack.app.presentation.components.type.EmphasisType
import com.dyentrack.app.presentation.components.type.VariantType
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.screens.home.component.ObjectifBox
import com.dyentrack.app.presentation.theme.secondaryTextColor

@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
) {

    val userPreference by userPreferenceViewModel.userPreference.collectAsState()

    val authState by authViewModel.authState.collectAsState()
    val state by homeViewModel.uiState.collectAsState()
    val objectifStep = userPreference?.objStep ?:0
    val objectifCal = userPreference?.objCalorie ?:0
    val objectifsommeil = userPreference?.objHourSleep ?:0.0

    val language = homeViewModel.languageUser
    var pourcentage = 0f
    state.todaySeance?.let { seance ->
        var pourcentage = 0f
        if (seance.seanceTrack?.plannedExercises?.isNotEmpty() == true) {
            pourcentage += 0.25f
        }
        seance.nutritionTrack?.calories?.takeIf { it > objectifCal }?.let { pourcentage += 0.25f }
        seance.physioTrack?.step?.takeIf { it > objectifCal }?.let { pourcentage += 0.25f }
        seance.physioTrack?.hourOfSleep?.takeIf { it > objectifCal }?.let { pourcentage += 0.25f }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier
            .fillMaxWidth()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            stringResource(R.string.hello) +",",
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                        )
                        Text(
                            "${authState.user?.pseudo ?:stringResource(R.string.prepare_day)} !",
                            style = MaterialTheme.typography.titleMedium,
                            modifier= Modifier.padding(top = 4.dp)
                        )
                    }
                    IconButton(
                        { navController.navigate("parametre") }, modifier = Modifier
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Go settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            item { Column(Modifier.fillMaxWidth().padding(start = 24.dp,end= 24.dp,top=32.dp,bottom=40.dp), verticalArrangement = Arrangement.spacedBy(8.dp) ){
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom){
                    Text(
                        stringResource(R.string.daily_data),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W600 )
                    )
                    Text(
                            "${(pourcentage*100).toInt()} %",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600),
                    )
                }
                ProgressLine(pourcentage, height = 8.dp)


            } }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val listState = rememberLazyListState()

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.todaySeance?.seanceTrack?.plannedExercises?.isEmpty() == true||state.todaySeance?.seanceTrack?.plannedExercises == null) {
                            item {

                                Box(
                                    modifier = Modifier
                                        .size(88.dp).clip(MaterialTheme.shapes.medium)
                                        .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 1f))
                                        .clickable(onClick = { navController.navigate("seance") }),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            tint = MaterialTheme.colorScheme.background,
                                            contentDescription = stringResource(R.string.add)
                                        )
                                        Text(
                                            stringResource(R.string.add_session),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.background,
                                                fontWeight = FontWeight.W600
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            val steps = remember(state.todaySeance) { state.todaySeance?.physioTrack?.step ?: 0 }
                            val progressSteps = remember(steps, objectifStep) {
                                if ( objectifStep == 0) {
                                    0f
                                } else {
                                    (steps.toFloat() / objectifStep.toFloat()).coerceIn(0f, 1f)
                                }
                            }

                            ObjectifBox(
                                value = steps.toString(),
                                objectif = if (objectifStep==0) "NaN" else objectifStep.toString(),
                                onClickObjectif = {navController.navigate("objectif")},
                                unit = stringResource(R.string.step),
                                onClick = {navController.navigate("seance?section=data")},
                                drawableTop =  R.drawable.icon_step

                            ) {
                                ProgressLine(
                                    progressSteps,
                                    height = 4.dp,
                                    progressColor = Color(0xFF3B5398),
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                        item {
                            val calories = remember(state.todaySeance) { state.todaySeance?.nutritionTrack?.calories ?: 0 }
                            val progressCalories = remember(calories, objectifCal) {
                                if (objectifCal == 0) {
                                    0f
                                } else {
                                    (calories.toFloat() / objectifCal.toFloat()).coerceIn(0f, 1f)
                                }
                            }
                            ObjectifBox(
                                value = calories.toString(),
                                objectif = if (objectifCal==0) "NaN" else objectifCal.toString(),
                                unit = "kcal",
                                onClick = {navController.navigate("seance?section=data")},
                                onClickObjectif = {navController.navigate("objectif")},
                                drawableTop =  R.drawable.icon_cal
                            ) {
                                ProgressLine(
                                    progressCalories,
                                    height = 4.dp,
                                    progressColor = Color(0xFFFF5915),
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                        item {
                            val sleep = remember(state.todaySeance) { state.todaySeance?.physioTrack?.hourOfSleep ?: 0 }
                            val progressSleep = remember(sleep, objectifsommeil) {
                                if (objectifsommeil == 0.0) {
                                    0f
                                } else {
                                    (sleep.toFloat() / objectifsommeil.toFloat()).coerceIn(0f, 1f)
                                }
                            }
                            ObjectifBox(
                                value = sleep.toString(),
                                objectif = if (objectifsommeil==0.0) "NaN" else objectifsommeil.toString(),
                                unit = "h",
                                onClick = {navController.navigate("seance?section=data")},
                                onClickObjectif = {navController.navigate("objectif")},
                                drawableTop =  R.drawable.icon_sleep
                            ) {
                                ProgressLine(
                                    progressSleep,
                                    height = 4.dp,
                                    progressColor = Color(0xFF6BECFF),
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val itemCount = if (state.todaySeance?.seanceTrack?.plannedExercises?.isEmpty() == true|| state.todaySeance?.seanceTrack?.plannedExercises ==null) 4 else 3


                        for (i in 0 until itemCount) {
                            val alpha = when {
                                i < listState.firstVisibleItemIndex -> 0.3f
                                i == listState.firstVisibleItemIndex -> {
                                    val scrollFraction = listState.firstVisibleItemScrollOffset / 200f
                                    1f - scrollFraction.coerceIn(0f, 0.7f)
                                }
                                else -> 1f
                            }

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                }
            }
            item {
                Column(
                    modifier = Modifier.padding(top = 32.dp, start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                        Text(stringResource(R.string.previous_workouts), Modifier.padding(bottom = 12.dp))
                    }


                    state.last5DaysKeys.forEachIndexed { index, day ->
                        val groupMuscles = state.muscleRateOfEachDay[day]
                            ?.keys
                            ?.mapNotNull { muscleId ->
                                val muscle = state.masterDataMaps?.muscleMap?.get(muscleId)
                                state.masterDataMaps?.musclesGroupMap?.get(muscle?.idMuscularGroup)
                            }
                            ?.toSet()
                            ?: emptySet()

                        CardWithContent(
                            modifier = Modifier.padding(8.dp, 2.dp),
                            text = groupMuscles.joinToString(", "),
                            day,
                            onExpandChange = { expanded ->
                                homeViewModel.expandSeance(if (expanded) index  else null)
                            },
                            canExtend = true,
                            expanded = state.expandedSeance == index ,

                            componentRight = {
                                Icon(
                                    if (state.expandedSeance == index ) {
                                        Icons.Default.ArrowDropUp
                                    } else {
                                        Icons.Default.ArrowDropDown
                                    },
                                    contentDescription = stringResource(
                                        if (state.expandedSeance == index) R.string.show_less else R.string.show_more
                                    )
                                )
                            },
                            leftIcon = homeViewModel.getLeftIconForSeance(state.mapSeance[day]),
                            hasLineShadowBetweenMainAndChildren = true,
                            childrenForExtend = {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp, bottom = 8.dp)
                                    ) {
                                        Text(
                                            pluralStringResource(R.plurals.muscle,2),
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                        Text(
                                            pluralStringResource(R.plurals.exercise,2),
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            pluralStringResource(R.plurals.set,2),
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.width(24.dp))
                                    }

                                    state.muscleRateOfEachDay[day]?.entries?.forEachIndexed { index, (key, value) ->
                                        val isLast =
                                            index == state.muscleRateOfEachDay[day]!!.size - 1
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp)
                                        ) {
                                            Text(
                                                state.masterDataMaps?.muscleMap?.get(key)?.getName(language) ?: "Not found",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                            )
                                            Text(
                                                value.numberExercise.toString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                value.numberSet.toString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                            if (isLast) {
                                                IconButton(
                                                    onClick = {navController.navigate("seance?dayKey=$day")},
                                                    modifier = Modifier.size(24.dp)
                                                ) { Icon(Icons.Default.Fullscreen, "go to seance") }
                                            } else {
                                                Spacer(modifier = Modifier.width(24.dp))
                                            }
                                        }
                                    }


                                }

                            }
                        )


                    }

                }
            }
            item {
                CustomButton(
                    stringResource(R.string.view_workouts),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 10.dp, end = 10.dp),
                    variant = VariantType.OUTLINED,
                    emphasis = EmphasisType.SECONDARY,
                    onClick = { navController.navigate("seance") }
                )
            }
            item {

                Column(Modifier.padding(top = 60.dp, bottom = 16.dp, start = 14.dp, end = 14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.analysis), style = MaterialTheme.typography.titleSmall)
                        Box(Modifier.clickable(onClick = {navController.navigate("analyse")})){

                            Text(
                                stringResource(R.string.see_more),
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            ) }
                    }
                    Text(
                        ""+ state.mapSeance.count() + " / 14 "+pluralStringResource(R.plurals.days,2),
                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(340.dp).padding(horizontal = 10.dp)


                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {

                    RadarChartSample(state.graphData, state.masterDataMaps?.musclesGroupMap)
                }

            }
        }
    }
}

@Composable
fun RadarChartSample(
    graphData: Map<Long, MuscleRate>,
    musclesGroupMap:Map<Long, String>?
) {
    val radarData = remember(graphData, musclesGroupMap) {
        val radarLabels = mutableListOf<String>()
        val values2 = mutableListOf<Float>()
        val values3 = mutableListOf<Float>()

        graphData.forEach { (muscleGroupId, stats) ->
            val groupName = musclesGroupMap?.get(muscleGroupId) ?: "N/A"
            radarLabels.add(groupName)
            values2.add(stats.numberExercise.toFloat())
            values3.add(stats.numberSet.toFloat())
        }

        Triple(radarLabels, values2, values3)
    }

    val radarLabels = radarData.first
    if (radarLabels.size < 3) {
        Text("Pas assez de données pour le radar")
        return
    }

    RadarChartMP(
        Modifier.fillMaxSize(),
        radarLabels,
        values1 = radarData.second,
        values2 = radarData.third
    )
}
