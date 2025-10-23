package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.usecase.GetLast30DaysUseCase
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.theme.addColor
import com.dyentrack.app.presentation.theme.secondaryTextColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSelectorRange(
    modifier: Modifier = Modifier,
    viewModel: SeanceViewModel = viewModel()
) {
    val days = viewModel.days
    val uiState by viewModel.uiState.collectAsState()

    LazyRow(
        reverseLayout = true,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(end = 8.dp, bottom = 24.dp)
    ) {
        items(days.size) { index ->
            val day = days[index]
            Column(
                modifier = Modifier.padding(4.dp, 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(day.shortDay, style = MaterialTheme.typography.bodySmall.copy(
                    color = if (uiState.daySelected == day.dateFormat) MaterialTheme.colorScheme.primary else secondaryTextColor)
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .then(
                            when {
                                uiState.daySelected == day.dateFormat -> {
                                    Modifier.border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                                }

                                uiState.seancesParJour[day.dateFormat]?.exerciseIds?.isNotEmpty() == true -> {
                                    Modifier.border(1.dp, secondaryTextColor, CircleShape)
                                }

                                else -> Modifier
                            }
                        )
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .clickable { viewModel.changeDaySelected(day.dateFormat) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(day.dayNumber.toString(), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
