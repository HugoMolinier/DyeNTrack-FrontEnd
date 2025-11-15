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
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.screens.seance.dataSection.component.NutrientRow
import com.dyentrack.app.presentation.screens.seance.viewModel.NutritionField
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun NutritionBox( viewModel: SeanceViewModel = viewModel(),nutritionTrack: NutritionTrack,lockInput:Boolean =false,day:String){
    val carbs = remember(day) {
        mutableStateOf(
            TextFieldValue(text = if ((nutritionTrack.carbohydrates ?: 0) == 0) "" else nutritionTrack.carbohydrates.toString())
        )
    }

    val prots = remember(day) {
        mutableStateOf(
            TextFieldValue(text = if ((nutritionTrack.proteins ?: 0) == 0) "" else nutritionTrack.proteins.toString())
        )
    }

    val fats = remember(day) {
        mutableStateOf(
            TextFieldValue(text = if ((nutritionTrack.lipids ?: 0) == 0) "" else nutritionTrack.lipids.toString())
        )
    }

    val fiber = remember(day){
        mutableStateOf(
            TextFieldValue(text = if ((nutritionTrack.fiber ?: 0) == 0) "" else nutritionTrack.fiber.toString())
        )
    }

    val cafeine = remember(day){
        mutableStateOf(
            TextFieldValue(text = if ((nutritionTrack.cafeins ?: 0) == 0) "" else nutritionTrack.cafeins.toString())
        )
    }

    val calories = remember(day){
        mutableStateOf(
            TextFieldValue(text = if ((nutritionTrack.calories ?: 0) == 0) "" else nutritionTrack.calories.toString())
        )
    }
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync,"sync with MyFitnessPal", tint = secondaryTextColor, modifier = Modifier.size(28.dp).padding(4.dp))
                        Text(" import from MyFitnessPal",style=MaterialTheme.typography.bodySmall.copy(color=MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)))
                    }
                    Column(
                        Modifier.padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End

                    ) {
                        NutrientRow(
                            label = "",
                            valueState = calories,
                            isEditable=!lockInput,
                            unit = "kcal",
                            modifierInput = Modifier.width(   100.dp),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.CALORIES, value)
                                }
                            }
                        )
                        Row(
                            Modifier.padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp
                            ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            val protsValue = prots.value.text.toFloatOrNull() ?: 0f
                            val carbsValue = carbs.value.text.toFloatOrNull() ?: 0f
                            val fatsValue = fats.value.text.toFloatOrNull() ?: 0f
                            Text(
                                text = "Calculé : ${(protsValue * 4 + carbsValue * 4 + fatsValue * 9).toInt()} kcal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = secondaryTextColor.copy(
                                        alpha = 0.5f
                                    )
                                )
                            )
                            Icon(
                                Icons.Default.Info,
                                "Afficher Calcul",
                                modifier = Modifier.size(20.dp),
                                tint = secondaryTextColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Row(Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(IntrinsicSize.Max)) {
                    Column(
                        Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        NutrientRow(
                            label = "Protéines",
                            valueState = prots,
                            isEditable=!lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.PROTEINS, value)
                                }
                            }
                        )
                        NutrientRow(
                            label = "Glucides",
                            valueState = carbs,
                            isEditable=!lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.CARBOHYDRATES, value)
                                }
                            }
                        )

                        NutrientRow(
                            label = "Lipides",
                            valueState = fats,
                            isEditable=!lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.LIPIDS, value)
                                }
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(secondaryTextColor.copy(alpha = 0.2f))
                    )
                    Column(
                        Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NutrientRow(
                            label = "Fibres",
                            valueState = fiber,
                            isEditable=!lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.FIBER, value)
                                }
                            }
                        )
                        NutrientRow(
                            label = "Caféine",
                            valueState = cafeine,
                            unit = "mg",
                            isEditable=!lockInput,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { value ->
                                    viewModel.updateNutrition(NutritionField.CAFEINS, value)
                                }
                            }
                        )


                    }
                }
            }


        }
    }
}