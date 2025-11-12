package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.presentation.screens.seance.SaveType
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSelectorRange(
    modifier: Modifier = Modifier,
    viewModel: SeanceViewModel = viewModel(),
    uiState: SeanceUiState
) {
    val days = viewModel.days
    val todayDate = LocalDate.now()
    val colorsheme = MaterialTheme.colorScheme
    val today = todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val listState = rememberLazyListState()


    val selectedIndex = days.indexOfFirst { it.dateFormat == uiState.daySelected }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(
                index = selectedIndex,
                scrollOffset = -450 // ajuste pour centrer approximativement
            )
        }
    }

    LazyRow(
        state = listState,
        reverseLayout = true,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(end = 8.dp, bottom = 24.dp)
            .height(68.dp)
    ) {
        items(days.size) { index ->
            val day = days[index]
            val isSelected = uiState.daySelected == day.dateFormat
            val hasSeance =
                uiState.seancesParJour[day.dateFormat]?.exercisesInSceance?.isNotEmpty() == true
            val isToday = today == day.dateFormat
            val isAfterToday = LocalDate.parse(day.dateFormat).isAfter(todayDate)
            val saveStatus = uiState.seancesParJour[day.dateFormat]?.saveAPI
            val dotColor = when (saveStatus) {
                SaveType.APISAVE -> Color(0xFF4CAF50)   // vert
                SaveType.LocalSAVE -> Color(0xFFFFC107) // jaune
                SaveType.NOTSAVE -> Color(0xFFF44336)   // rouge
                else -> null
            }
            val (borderColor, backgroundColor, textColor) = remember(
                isSelected,
                hasSeance,
                isToday,
                isAfterToday
            ) {
                val border =
                    if (isSelected || isToday || hasSeance) colorsheme.primary else Color.Transparent
                val background = when {
                    isSelected -> colorsheme.primary.copy(alpha = 0.5f)
                    hasSeance -> colorsheme.primary.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
                val text = when {
                    isSelected -> colorsheme.onSurface
                    hasSeance -> colorsheme.primary
                    isAfterToday -> colorsheme.onSurface.copy(alpha = 0.5f)
                    else -> colorsheme.onSurface
                }
                Triple(border, background, text)
            }

            Column(
                modifier = Modifier.padding(4.dp, 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = day.shortDay,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isSelected)
                            colorsheme.primary
                        else
                            secondaryTextColor
                    )
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(1.dp, borderColor, CircleShape)
                        .background(backgroundColor, CircleShape)
                        .clickable { viewModel.changeDaySelected(day.dateFormat) },
                    contentAlignment = Alignment.Center
                ) {
                    if (dotColor != null ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .align(Alignment.TopEnd)
                                .background(dotColor, CircleShape)
                        )
                    }
                    Text(
                        day.dayNumber.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(color = textColor)
                    )
                }

            }
            if (day.dayNumber == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 16.dp, top = 16.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerticalDivider(
                        modifier = Modifier
                            .width(3.dp)
                            .weight(1f),
                        color = secondaryTextColor.copy(alpha = 0.5f)
                    )
                    Text(
                        text = day.month,
                        overflow = TextOverflow.Visible,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = secondaryTextColor.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }
    }
}
