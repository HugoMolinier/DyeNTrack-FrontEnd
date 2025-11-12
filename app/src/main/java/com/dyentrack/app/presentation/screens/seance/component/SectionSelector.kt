package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.theme.TertiaOne

@Composable
fun SectionSelector(modifier: Modifier = Modifier,itemSection : List<MenuItemData>?=null,selectedIndex :Int? = 0) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 5.dp, end = 12.dp, bottom = 20.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = TertiaOne,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }) {

        itemSection?.forEachIndexed {index, (label, onClick, _) ->
            val isSelected= index==selectedIndex
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable(enabled = !isSelected,onClick= {onClick.invoke()})
                    .then(if (isSelected) Modifier.drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        drawLine(
                            color = colorScheme.primary,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                    }else Modifier)


            ) {
                Text(
                    label,
                    textAlign = TextAlign.Center,
                    style =  MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isSelected) 1f else 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

            }
        }

    }
}