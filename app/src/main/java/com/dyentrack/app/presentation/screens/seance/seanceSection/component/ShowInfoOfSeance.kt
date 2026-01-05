package com.dyentrack.app.presentation.screens.seance.seanceSection.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.utils.convertWeight

@Composable
fun ShowInfoOfSeance(viewModel: SeanceViewModel, userUnit: MesureUnit) {

    val state by viewModel.uiState.collectAsState()


    val inputstate = remember {
        mutableStateOf(
            TextFieldValue(
                text = "",

                )
        )
    }


    val exerciseSummary by remember(state.daySelected) {
        mutableStateOf(viewModel.getSeanceSummary(state.daySelected))
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
        Text(
            stringResource(R.string.seance_detail),
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        InfoRow(label = stringResource(R.string.muscle_use), value = exerciseSummary.principalMuscle.joinToString(", "))
        InfoRow(label = stringResource(R.string.filter_muscle_groups), value = exerciseSummary.mainFocus.joinToString(", "), border = true)
        InfoRow(
            label = stringResource(R.string.number_of_sets),
            value = exerciseSummary.totalSets.toString()
        )
        InfoRow(
            label = stringResource(R.string.number_of_exercises),
            value = exerciseSummary.exercisesList.count().toString(),
            border = true
        )
        InfoRow(
            label = stringResource(R.string.weighted_sum),
            value = convertWeight(exerciseSummary.sommePond.toDouble(), MesureUnit.KG, userUnit)
                ?.let { String.format("%.1f", it) }
                .orEmpty() + userUnit.toString().lowercase()
        )

    }
}
