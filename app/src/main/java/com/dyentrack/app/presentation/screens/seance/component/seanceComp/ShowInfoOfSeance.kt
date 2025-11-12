package com.dyentrack.app.presentation.screens.seance.component.seanceComp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel

@Composable
fun ShowInfoOfSeance(viewModel: SeanceViewModel) {

    val state by viewModel.uiState.collectAsState()


    val inputstate = remember {
        mutableStateOf(
            TextFieldValue(
                text = "",

                )
        )
    }


    val (exercises, mainFocusOfSeance, totalSets) = remember(state) {
        val exercisesList = viewModel.masterData?.officialExercise
            ?.filter { exercise ->
                state.seancesParJour[state.daySelected]?.exercisesInSceance
                    ?.any { it.exerciseId == exercise.idExercise } ?: false
            } ?: listOf()

        val mainFocusSet: Set<String> = run {
            val mainFocusIds = exercisesList.mapNotNull { it.mainFocusGroup }.toSet()
            viewModel.masterData?.musclesGroup
                ?.filter { it.id in mainFocusIds }
                ?.map { it.nameFR }
                ?.toSet()
                ?: emptySet()
        }
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
        InfoRow(label = "Nombre de série", value = totalSets.toString(), border = true)
        InfoRow(label = "Nombre d'exercices", value = exercises.count().toString())

        InputCustom(
            modifier = Modifier
                .padding(top = 12.dp)
                .height(70.dp),
            inputstate,
            placeholder = "Ajouter une note",
            variant = InputVariant.OUTLINED,
            singleLine = false
        )
    }
}
