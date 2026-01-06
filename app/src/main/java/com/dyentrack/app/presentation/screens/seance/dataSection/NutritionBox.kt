package com.dyentrack.app.presentation.screens.seance.dataSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.enum.NutritionField
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.screens.seance.dataSection.component.NutrientRow
import com.dyentrack.app.presentation.screens.seance.SeanceDuJour
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun NutritionBox(
    viewModel: SeanceViewModel = viewModel(),
    state: SeanceUiState,
    lockInput: Boolean = false,
    day: String,
    isAutoSync:Boolean
) {
    val currentSeance = state.seancesParJour[day] ?: SeanceDuJour()
    val nutritionTrack = currentSeance.nutritionTrack

    // --- UI State pour TextFieldValue, stable pour le curseur ---
    val carbs = remember { mutableStateOf(TextFieldValue("")) }
    val prots = remember { mutableStateOf(TextFieldValue("")) }
    val fats = remember { mutableStateOf(TextFieldValue("")) }
    val fiber = remember { mutableStateOf(TextFieldValue("")) }
    val cafeine = remember { mutableStateOf(TextFieldValue("")) }
    val calories = remember { mutableStateOf(TextFieldValue("")) }

    // --- Met à jour les TextFieldValues uniquement si la valeur change réellement ---
    LaunchedEffect(nutritionTrack) {
        val newCarbs = if ((nutritionTrack.carbohydrates ?: 0) == 0) "" else nutritionTrack.carbohydrates.toString()
        if (carbs.value.text != newCarbs) carbs.value = TextFieldValue(newCarbs)

        val newProts = if ((nutritionTrack.proteins ?: 0) == 0) "" else nutritionTrack.proteins.toString()
        if (prots.value.text != newProts) prots.value = TextFieldValue(newProts)

        val newFats = if ((nutritionTrack.lipids ?: 0) == 0) "" else nutritionTrack.lipids.toString()
        if (fats.value.text != newFats) fats.value = TextFieldValue(newFats)

        val newFiber = if ((nutritionTrack.fiber ?: 0) == 0) "" else nutritionTrack.fiber.toString()
        if (fiber.value.text != newFiber) fiber.value = TextFieldValue(newFiber)

        val newCafeine = if ((nutritionTrack.cafeins ?: 0) == 0) "" else nutritionTrack.cafeins.toString()
        if (cafeine.value.text != newCafeine) cafeine.value = TextFieldValue(newCafeine)

        val newCalories = if ((nutritionTrack.calories ?: 0) == 0) "" else nutritionTrack.calories.toString()
        if (calories.value.text != newCalories) calories.value = TextFieldValue(newCalories)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
    ) {

            Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {

                // --- Ligne supérieure avec calories ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                color = secondaryTextColor.copy(alpha = 0.2f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        Modifier.padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        NutrientRow(
                            label = "",
                            valueState = calories,
                            isEditable = !lockInput,
                            unit = "kcal",
                            modifierInput = Modifier.width(100.dp),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.CALORIES, value)
                                }
                            },
                            autoSync = isAutoSync
                        )

                        Row(
                            Modifier.padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val protsValue = prots.value.text.toFloatOrNull() ?: 0f
                            val carbsValue = carbs.value.text.toFloatOrNull() ?: 0f
                            val fatsValue = fats.value.text.toFloatOrNull() ?: 0f
                            Text(
                                text = stringResource(R.string.calculated)+" ${(protsValue * 4 + carbsValue * 4 + fatsValue * 9).toInt()} kcal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = secondaryTextColor.copy(alpha = 0.5f)
                                )
                            )
                            Icon(
                                Icons.Default.Info,
                                stringResource(R.string.show_calculation),
                                modifier = Modifier.size(20.dp),
                                tint = secondaryTextColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // --- Colonnes nutriments ---
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .height(IntrinsicSize.Max)
                ) {
                    // Colonne de gauche
                    Column(
                        Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NutrientRow(
                            label = stringResource(R.string.proteins),
                            valueState = prots,
                            isEditable = !lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.PROTEINS, value)
                                }
                            },
                            autoSync = isAutoSync
                        )
                        NutrientRow(
                            label = stringResource(R.string.carbohydrates),
                            valueState = carbs,
                            isEditable = !lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.CARBOHYDRATES, value)
                                }
                            },
                            autoSync = isAutoSync
                        )
                        NutrientRow(
                            label = stringResource(R.string.lipids),
                            valueState = fats,
                            isEditable = !lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.LIPIDS, value)
                                }
                            },
                            autoSync = isAutoSync
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(secondaryTextColor.copy(alpha = 0.2f))
                    )

                    // Colonne de droite
                    Column(
                        Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NutrientRow(
                            label = stringResource(R.string.fiber),
                            valueState = fiber,
                            isEditable = !lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.FIBER, value)
                                }
                            },
                            autoSync = isAutoSync
                        )
                        NutrientRow(
                            label = stringResource(R.string.caffeine),
                            valueState = cafeine,
                            unit = "mg",
                            isEditable = !lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.CAFEINS, value)
                                }
                            },
                            autoSync = isAutoSync
                        )
                    }
                }
            }
    }
}
