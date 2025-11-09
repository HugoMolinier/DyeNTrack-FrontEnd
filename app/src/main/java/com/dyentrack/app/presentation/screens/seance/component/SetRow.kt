package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.screens.seance.SaveType
import com.dyentrack.app.presentation.theme.addColor
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel

@Composable
fun SetRow(
    set: SetData,
    index: Int,
    viewModel: SeanceViewModel = viewModel(),
    setRightForUni: SetData? = null,
    lockInput: Boolean
) {
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
    val repsRightState = remember(setRightForUni) {
        mutableStateOf(TextFieldValue(text = setRightForUni?.reps?.toString() ?: ""))
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
                .height(24.dp),
            containerColor = when (set.type) {

                SetType.EFFECTIVE -> if (rirState.value.text.toIntOrNull() == 0) addColor.setEch else MaterialTheme.colorScheme.primary
                else -> addColor.setWarm
            },
            contentColor = when (set.type) {
                SetType.WARMUP -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.background
            },
        )

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(2.dp)) {


            InputCustom(
                text = repsState,
                enabled = !lockInput,
                placeholder = if (setRightForUni != null) "L" else "Reps",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                onValueChange = { viewModel.changeSaveStatus(SaveType.NOTSAVE) }
            )
            LaunchedEffect(repsState.value.text) {
                set.reps = repsState.value.text.toIntOrNull() ?: 0
            }
            if (setRightForUni != null) {
                InputCustom(
                    text = repsRightState,
                    placeholder = "R",
                    enabled = !lockInput,
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number,
                    onValueChange = { viewModel.changeSaveStatus(SaveType.NOTSAVE) }
                )
                LaunchedEffect(repsRightState.value.text) {
                    setRightForUni.reps = repsRightState.value.text.toIntOrNull() ?: 0
                }
            }
        }

        InputCustom(
            text = chargeState,
            placeholder = "Charge",
            enabled = !lockInput,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Decimal,
            onValueChange = { viewModel.changeSaveStatus(SaveType.NOTSAVE) }

        )
        LaunchedEffect(chargeState.value.text) {
            val value = chargeState.value.text.toDoubleOrNull()
            set.charge = value
            setRightForUni?.charge = value
        }



        InputCustom(
            text = rirState,
            placeholder = "RIR",
            enabled = !lockInput,
            modifier = Modifier.width(65.dp),
            keyboardType = KeyboardType.Number,
            onValueChange = { viewModel.changeSaveStatus(SaveType.NOTSAVE) }
            , onFocusLost =
        )
        LaunchedEffect(rirState.value.text) {
            val rirValue = rirState.value.text.toIntOrNull() ?: 0
            set.rir = rirValue
            setRightForUni?.rir = rirValue

        }

    }

}