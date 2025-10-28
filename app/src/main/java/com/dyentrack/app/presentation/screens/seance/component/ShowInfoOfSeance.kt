package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
@Composable
fun ShowInfoOfSeance(viewModel: SeanceViewModel = viewModel()) {

    val state by viewModel.uiState.collectAsState()


    val inputstate = remember {
        mutableStateOf(
            TextFieldValue(
                text =  "",

                )
        )
    }


    val (exercises, mainFocusOfSeance, totalSets) = remember (state) {
        val exercisesList = state.masterDataStatic.officialExercise
            .filter { it.idExercise in state.seancesParJour[state.daySelected]?.exerciseIds.orEmpty() }

        val mainFocusSet: Set<String> = state.masterDataStatic.musclesGroup
            .filter { it.id in exercisesList.mapNotNull { it.mainFocusGroup }.toSet() }
            .map { it.nameFR }
            .toSet()

        val total = state.seancesParJour[state.daySelected]?.setsMap?.values
            ?.sumOf { it.size } ?: 0

        Triple(exercisesList, mainFocusSet, total)
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
        Text(
            "Détails de la séance",
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        InfoRow(label = "Muscle utilisé", value = mainFocusOfSeance.joinToString(", "))
        InfoRow(label = "Nombre de série", value = totalSets.toString(),true)
        InfoRow(label = "Nombre d'exercices", value = exercises.count().toString())

        InputCustom(modifier = Modifier.padding(top=12.dp).height(70.dp), inputstate, placeholder = "Ajouter une note", variant = InputVariant.OUTLINED, singleLine = false)
    }
}
