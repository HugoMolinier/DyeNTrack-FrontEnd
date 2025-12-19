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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dyentrack.app.R
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.presentation.components.ButtonEmphasis
import com.dyentrack.app.presentation.components.ButtonVariant
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.components.ProgressLine
import com.dyentrack.app.presentation.components.chart.RadarChartMP
import com.dyentrack.app.presentation.screens.home.component.ObjectifBox
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.ExerciseImages

@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    navController: NavController
) {

    val state by homeViewModel.uiState.collectAsState()
    val pseudo = "Pearlt"
    val objectifStep = 15000
    val objectifCal = 3000
    val objectifsommeil = 8.0

    var pourcentage = 0f
    if (state.todaySeance?.seanceTrack?.plannedExercises?.isNotEmpty() == true) {
        pourcentage += 0.25f
    }
    if (state.todaySeance?.nutritionTrack?.calories !=0 && state.todaySeance?.nutritionTrack?.calories !=null ) {
        pourcentage += 0.25f
    }
    if (state.todaySeance?.physioTrack?.step !=0 &&state.todaySeance?.physioTrack?.step !=null) {
        pourcentage += 0.25f
    }
    if (state.todaySeance?.physioTrack?.hourOfSleep !=0.0&&state.todaySeance?.physioTrack?.hourOfSleep !=null) {
        pourcentage += 0.25f
    }

    val muscleName: Map<Long, String> = homeViewModel.masterData?.muscles
        ?.associate { muscle -> muscle.id to muscle.nameFR }
        ?: emptyMap()
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
                            "Bonjour,",
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                        )
                        Text(
                            "$pseudo !",
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
                        "Données du jour ",
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
                                            contentDescription = "ajouter une séance"
                                        )
                                        Text(
                                            "Ajouter une séance",
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
                            val progressSteps = remember(steps, objectifStep) { (steps.toFloat() / objectifStep.toFloat()).coerceIn(0f, 1f) }

                            ObjectifBox(
                                value = steps.toString(),
                                objectif = objectifStep.toString(),
                                unit = "pas",
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
                            val progressCalories = remember(calories, objectifCal) { (calories.toFloat() / objectifCal.toFloat()).coerceIn(0f, 1f) }
                            ObjectifBox(
                                value = calories.toString(),
                                objectif = objectifCal.toString(),
                                unit = "Cal",
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
                            val progressSleep = remember(sleep, objectifsommeil) { (sleep.toFloat() / objectifsommeil.toFloat()).coerceIn(0f, 1f) }
                            ObjectifBox(
                                value = sleep.toString(),
                                objectif = objectifsommeil.toString(),
                                unit = "h",
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
                        Text("Scéances précédentes", Modifier.padding(bottom = 12.dp))
                    }
                    for (i in state.mapSeance.size - 1 downTo (state.mapSeance.size - 5).coerceAtLeast(
                        0
                    )) {
                        val dayKey = state.mapSeance.keys.elementAt(i)
                        val setGroupMuscu: MutableSet<String> = mutableSetOf()

                        state.muscleRateOfEachDay[dayKey]?.keys?.forEach { muscleId ->
                            val muscle =
                                homeViewModel.masterData?.muscles?.find { it.id == muscleId }
                            val groupeMusculaire =
                                homeViewModel.masterData?.musclesGroup?.find { it.id == muscle?.idMuscularGroup }
                            groupeMusculaire?.nameFR?.let { setGroupMuscu.add(it) }
                        }

                        CardWithContent(
                            modifier = Modifier.padding(8.dp, 2.dp),
                            text = setGroupMuscu.joinToString(", "),
                            dayKey,
                            onExpandChange = { expanded ->
                                homeViewModel.expandSeance(if (expanded) i else null)
                            },
                            canExtend = true,
                            expanded = state.expandedSeance == i,

                            componentRight = {
                                Icon(
                                    if (state.expandedSeance == i) {
                                        Icons.Default.ArrowDropUp
                                    } else {
                                        Icons.Default.ArrowDropDown
                                    }, ""
                                )
                            },
                            leftIcon = homeViewModel.getLeftIconForSeance(state.mapSeance[dayKey]),
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
                                            "Muscles",
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                        Text(
                                            "Exercice",
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            "Série",
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.width(24.dp))
                                    }

                                    state.muscleRateOfEachDay[dayKey]?.entries?.forEachIndexed { index, (key, value) ->
                                        val isLast =
                                            index == state.muscleRateOfEachDay[dayKey]!!.size - 1
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp)
                                        ) {
                                            Text(
                                                muscleName[key] ?: "Not found",
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
                                                    onClick = {navController.navigate("seance?dayKey=$dayKey")},
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
                    "Voir les séances",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 10.dp, end = 10.dp),
                    variant = ButtonVariant.OUTLINED,
                    emphasis = ButtonEmphasis.SECONDARY,
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
                        Text("Analyse", style = MaterialTheme.typography.titleSmall)
                        Box(Modifier.clickable(onClick = {navController.navigate("analyse")})){

                            Text(
                                "Voir plus",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            ) }
                    }
                    Text(
                        ""+ state.mapSeance.count() + " / 14 jours",
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

                    RadarChartSample(state.graphData, homeViewModel.masterData)
                }

            }
        }
    }
}

@Composable
fun RadarChartSample(graphData:Map<Long, MuscleRate>, masterData: MasterDataStatic?) {

    val radarData = remember(graphData) {
        val radarLabels = mutableListOf<String>()
        val values2 = mutableListOf<Float>()
        val values3 = mutableListOf<Float>()

        graphData.forEach { (muscleGroupId, stats) ->
            val groupName =
                masterData?.musclesGroup?.find { it.id == muscleGroupId }?.nameFR
                    ?: "N/A"
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
