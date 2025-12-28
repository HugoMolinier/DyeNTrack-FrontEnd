package com.dyentrack.app.presentation.screens.seance.seanceSection.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetField
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.theme.addColor
import com.dyentrack.app.presentation.utils.convertWeight
import kotlinx.coroutines.delay

@Composable
fun SetRow(
    set: SetData,
    index: Int,
    viewModel: SeanceViewModel = viewModel(),
    lockInput: Boolean,
    onClickBadge: (() -> Unit)? = null,
    exerciseId: Long,
    userUnit: MesureUnit
) {
    val chargeState = remember {
        mutableStateOf(
            TextFieldValue(
                text = set.charge
                    ?.let { convertWeight(it, MesureUnit.KG, userUnit) }
                    ?.let { String.format("%.1f", it) }
                    ?: ""
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

    val repsRightState = remember {
        mutableStateOf(TextFieldValue(text = set.linkedRightSet?.reps?.toString() ?: ""))
    }

    var confirmDelete by remember { mutableStateOf(false) }
    if (confirmDelete) {
        LaunchedEffect(true) {
            delay(3000)
            confirmDelete = false
        }
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
                Text(
                    text = if (set.type == SetType.EFFECTIVE) {
                        if (confirmDelete) "-" else (index + 1).toString()
                    } else set.type.toString().uppercase()
                )
            },
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .clickable(enabled = !lockInput && onClickBadge != null) {
                    if (!confirmDelete) {
                        confirmDelete = true
                    } else {
                        onClickBadge?.invoke()
                        confirmDelete = false
                    }

                }
                .then(
                    if (confirmDelete) {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    } else {
                        Modifier
                    }
                ),
            containerColor = if (confirmDelete) {
                Color.Transparent
            } else {
                when (set.type) {
                    SetType.EFFECTIVE -> if (rirState.value.text.toIntOrNull() == 0) addColor.setEch else MaterialTheme.colorScheme.primary
                    else -> addColor.setWarm
                }
            },
            contentColor = if (confirmDelete) {
                MaterialTheme.colorScheme.primary
            } else {
                when (set.type) {
                    SetType.WARMUP -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.background
                }
            },
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {


            InputCustom(
                text = repsState,
                enabled = !lockInput,
                placeholder = if (set.linkedRightSet != null) "L" else "Reps",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                maxCaracter = 2,
                replaceZeroToNull = true,
                onValueChange = {

                    viewModel.updateSet(
                        exerciseId = exerciseId,
                        index = index,
                        newValue = SetField.Reps(repsState.value.text.toIntOrNull()),
                        rightSet = false,
                        unilateral = set.linkedRightSet != null
                    )
                }
            )

            if (set.linkedRightSet != null) {
                InputCustom(
                    text = repsRightState,
                    placeholder = "R",
                    enabled = !lockInput,
                    modifier = Modifier.weight(1f),
                    maxCaracter = 2,
                    replaceZeroToNull = true,
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        viewModel.updateSet(
                            exerciseId = exerciseId,
                            index = index,
                            newValue = SetField.Reps(repsRightState.value.text.toIntOrNull()),
                            rightSet = true,
                            unilateral = true
                        )
                    }
                )

            }
        }

        InputCustom(
            text = chargeState,
            placeholder = "Charge",
            enabled = !lockInput,
            replaceZeroToNull = true,
            modifier = Modifier.weight(1f),
            maxCaracter = 6,
            keyboardType = KeyboardType.Decimal,
            onValueChange = {
                viewModel.updateSet(
                    exerciseId = exerciseId,
                    index = index,
                    newValue = SetField.Charge(
                        convertWeight(
                            chargeState.value.text.toDoubleOrNull(),
                            userUnit,
                            MesureUnit.KG
                        )

                    ),
                    rightSet = false,
                    unilateral = set.linkedRightSet != null
                )
            }

        )



        InputCustom(
            text = rirState,
            placeholder = "RIR",
            enabled = !lockInput,
            modifier = Modifier.width(65.dp),
            keyboardType = KeyboardType.Number,
            maxCaracter = 2,
            onValueChange = {
                viewModel.updateSet(
                    exerciseId = exerciseId,
                    index = index,
                    newValue = SetField.Rir(rirState.value.text.toIntOrNull()),
                    rightSet = false,
                    unilateral = set.linkedRightSet != null
                )
            }
        )


    }

}