package com.dyentrack.app.presentation.screens.seance.dataSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.enum.PhysioField
import com.dyentrack.app.presentation.screens.seance.dataSection.component.NutrientRow
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceDuJour
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceUiState
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.convertWeight

@Composable
fun PhysioBox(
    viewModel: SeanceViewModel = viewModel(),
    state: SeanceUiState,
    lockInput: Boolean = false,
    day: String,
    isAutoSync:Boolean,
    userUnit: MesureUnit
) {
    val currentSeance = state.seancesParJour[day] ?: SeanceDuJour()
    val physioTrack = currentSeance.physioTrack

    // --- UI State pour TextFieldValue ---
    val steps = remember { mutableStateOf(TextFieldValue("")) }
    val sleep = remember { mutableStateOf(TextFieldValue("")) }
    val weight = remember { mutableStateOf(TextFieldValue("")) }

    // --- Met à jour uniquement si la valeur a changé ---
    LaunchedEffect(physioTrack) {
        val newSteps = if ((physioTrack.step ?: 0) == 0) "" else physioTrack.step.toString()
        if (steps.value.text != newSteps) steps.value = TextFieldValue(newSteps)

        val newSleep = if ((physioTrack.hourOfSleep ?: 0) == 0) "" else physioTrack.hourOfSleep.toString()
        if (sleep.value.text != newSleep) sleep.value = TextFieldValue(newSleep)

        val newWeight = if ((physioTrack.weight ?: 0) == 0) "" else convertWeight(physioTrack.weight, MesureUnit.KG, userUnit)?.let { String.format("%.1f", it) }.orEmpty()
        if (weight.value.text != newWeight) weight.value = TextFieldValue(newWeight)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {

            Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val yBottom = size.height - 0.5f
                            drawLine(
                                color = secondaryTextColor.copy(alpha = 0.3f),
                                start = Offset(0f, yBottom),
                                end = Offset(size.width, yBottom),
                                strokeWidth = 1f
                            )
                        }
                ) {
                    NutrientRow(
                        label = stringResource(R.string.number_steps),
                        valueState = steps,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                        modifierInput = Modifier.width(120.dp),
                        unit = stringResource(R.string.step).lowercase(),
                        isEditable = !lockInput,
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { value ->
                                viewModel.updatePhysio(PhysioField.STEP, value)
                            }
                        },
                        autoSync = isAutoSync
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val yTop = 0.5f
                            val yBottom = size.height - 0.5f
                            val color = secondaryTextColor.copy(alpha = 0.3f)
                            drawLine(color, Offset(0f, yTop), Offset(size.width, yTop), 1f)
                            drawLine(color, Offset(0f, yBottom), Offset(size.width, yBottom), 1f)
                        }
                ) {
                    NutrientRow(
                        label = stringResource(R.string.sleep),
                        valueState = sleep,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                        modifierInput = Modifier.width(120.dp),
                        unit = "h",
                        isEditable = !lockInput,
                        onValueChange = { newValue ->
                            newValue.toFloatOrNull()?.let { value ->
                                viewModel.updatePhysio(PhysioField.HOUR_OF_SLEEP, value)
                            }
                        },
                        autoSync = isAutoSync
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val yTop = 0.5f
                            drawLine(
                                color = secondaryTextColor.copy(alpha = 0.3f),
                                start = Offset(0f, yTop),
                                end = Offset(size.width, yTop),
                                strokeWidth = 1f
                            )
                        }
                ) {
                    NutrientRow(
                        label = stringResource(R.string.weight),
                        valueState = weight,
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        modifierInput = Modifier.width(120.dp),
                        unit = userUnit.toString().lowercase(),
                        isEditable = !lockInput,
                        onValueChange = { newValue ->
                            newValue.toFloatOrNull()?.let { value ->
                                convertWeight(value.toDouble(), userUnit, MesureUnit.KG)?.let { kgValue ->
                                    viewModel.updatePhysio(PhysioField.WEIGHT, kgValue.toFloat())
                                }
                            }
                        },
                        autoSync = false
                    )
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, top = 4.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        stringResource(R.string.last),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = secondaryTextColor,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        "73.1 kg (2025-12-12)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = secondaryTextColor,
                            fontSize = 10.sp
                        )
                    )
                }

            }

    }

}