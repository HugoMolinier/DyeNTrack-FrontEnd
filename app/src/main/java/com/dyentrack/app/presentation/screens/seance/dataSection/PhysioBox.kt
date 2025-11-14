package com.dyentrack.app.presentation.screens.seance.dataSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.PhysioTrack
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.screens.seance.dataSection.component.NutrientRow
import com.dyentrack.app.presentation.screens.seance.viewModel.PhysioField
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun PhysioBox( viewModel: SeanceViewModel = viewModel(),physioTrack: PhysioTrack,lockInput:Boolean =false){
    val steps = remember {
        mutableStateOf(
            TextFieldValue(text = if ((physioTrack.step ?: 0) == 0) "" else physioTrack.step.toString())
        )
    }
    val sleep = remember {
        mutableStateOf(
            TextFieldValue(text = if ((physioTrack.hourOfSleep ?: 0) == 0) "" else physioTrack.hourOfSleep.toString())
        )
    }
    val weight = remember {
        mutableStateOf(
            TextFieldValue(text = if ((physioTrack.weight ?: 0) == 0) "" else physioTrack.weight.toString())
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
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,modifier=Modifier.padding(bottom = 20.dp)) {
                        Icon(
                            Icons.Default.Sync,
                            "sync with MyFitnessPal",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(28.dp).padding(4.dp)
                        )
                        Text(
                            " import from healthApp",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val yTop = 0f + 1 / 2
                            val yBottom = size.height - 1 / 2
                            val color = secondaryTextColor.copy(alpha = 0.3f)

                            // Ligne du haut
                            drawLine(
                                color = color,
                                start = Offset(0f, yTop),
                                end = Offset(size.width, yTop),
                                strokeWidth = 1f

                            )

                            // Ligne du bas
                            drawLine(
                                color = color,
                                start = Offset(0f, yBottom),
                                end = Offset(size.width, yBottom),
                                strokeWidth = 1f
                            )
                        }
                ) {
                    NutrientRow("Nombre de pas", steps, modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp), modifierInput = Modifier.width(120.dp), unit = "steps", isEditable = !lockInput,

                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { value ->
                                viewModel.updatePhysio(PhysioField.STEP, value)
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val yTop = 0f + 1 / 2
                            val yBottom = size.height - 1 / 2
                            val color = secondaryTextColor.copy(alpha = 0.3f)

                            // Ligne du haut
                            drawLine(
                                color = color,
                                start = Offset(0f, yTop),
                                end = Offset(size.width, yTop),
                                strokeWidth = 1f

                            )

                            // Ligne du bas
                            drawLine(
                                color = color,
                                start = Offset(0f, yBottom),
                                end = Offset(size.width, yBottom),
                                strokeWidth = 1f
                            )
                        }
                ) {
                    NutrientRow("Sommeil", sleep, modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp), modifierInput = Modifier.width(120.dp), unit = "h", isEditable = !lockInput,
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { value ->
                                viewModel.updatePhysio(PhysioField.HOUR_OF_SLEEP, value)
                            }
                        })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val yTop = 0f + 1 / 2
                            val color = secondaryTextColor.copy(alpha = 0.3f)

                            // Ligne du haut
                            drawLine(
                                color = color,
                                start = Offset(0f, yTop),
                                end = Offset(size.width, yTop),
                                strokeWidth = 1f

                            )
                        }
                ) {
                    NutrientRow("Poids (Optionnel)", weight, modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp), modifierInput = Modifier.width(120.dp), unit = "kg", isEditable = !lockInput,
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { value ->
                                viewModel.updatePhysio(PhysioField.WEIGHT, value)
                            }
                        })
                }
                Row (Modifier.fillMaxWidth().padding(end = 16.dp, top = 4.dp, bottom = 16.dp), horizontalArrangement = Arrangement.End){
                    Text("Last : ",style=MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor, fontSize = 10.sp))
                    Text("73.1 kg (2025-12-12)",style=MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor, fontSize = 10.sp))

                }
            }
        }
    }
}