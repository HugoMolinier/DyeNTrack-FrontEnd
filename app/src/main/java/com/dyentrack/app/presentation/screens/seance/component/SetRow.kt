package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel

@Composable
fun SetRow(set: SetData,index: Int, viewModel: SeanceViewModel = viewModel()){
    val chargeState = remember {
        mutableStateOf(
            TextFieldValue(
                text = set.charge?.toString() ?: "",

            )
        )
    }

    val rirState = remember {
        mutableStateOf(
            TextFieldValue(
                text = set.rir?.toString() ?: "",

            )
        )
    }

    val repsState = remember {
        mutableStateOf(
            TextFieldValue(
                text = set.reps?.toString() ?: "",
            )
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Badge(
            content = {
                if (set.type == SetType.EFFECTIVE) Text((index + 1).toString())
                else Text(set.type.toString().uppercase())
            },
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
        )

        InputCustom(
            text = chargeState,
            placeholder = "Charge",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Decimal,
                    onValueChange = {viewModel.changeSaveStatus(false)}

        )
        LaunchedEffect(chargeState.value.text) {
            set.charge =
                chargeState.value.text.toDoubleOrNull()
        }



        InputCustom(
            text = repsState,
            placeholder = "Reps",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            onValueChange = {viewModel.changeSaveStatus(false)}
        )
        LaunchedEffect(repsState.value.text) {
            set.reps = repsState.value.text.toIntOrNull() ?: 0
        }
        InputCustom(
            text = rirState,
            placeholder = "RIR",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            onValueChange = {viewModel.changeSaveStatus(false)}
        )
        LaunchedEffect(rirState.value.text) {
            set.rir = rirState.value.text.toIntOrNull() ?: 0
        }

    }

}