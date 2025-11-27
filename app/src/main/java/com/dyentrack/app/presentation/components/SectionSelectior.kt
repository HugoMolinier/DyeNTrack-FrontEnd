package com.dyentrack.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.TertiaOne

enum class VariantSelection { OUTLINE, FULL }

@Composable
fun SectionSelector(
    modifier: Modifier = Modifier,
    itemSection: List<MenuItemData>? = null,
    selectedIndex: Int? = 0,
    variant: VariantSelection = VariantSelection.FULL
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (variant == VariantSelection.OUTLINE) {
                    Modifier.drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        drawLine(
                            color = TertiaOne,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
                } else {
                    Modifier.background(
                        TertiaOne,
                        shape = RoundedCornerShape(6.dp)
                    )
                }

            )
    ) {

        itemSection?.forEachIndexed { index, (label, onClick, _) ->
            val isSelected = index == selectedIndex
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)

                    .clickable(enabled = !isSelected, onClick = { onClick.invoke() })

                    .then(if (variant== VariantSelection.OUTLINE){
                        if (isSelected) Modifier.padding(horizontal = 8.dp, vertical = 4.dp).drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        drawLine(
                            color = colorScheme.primary,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                    } else Modifier                    .padding(horizontal = 8.dp, vertical = 4.dp)}else{
                        if (isSelected){
                            Modifier.padding(2.dp).background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(6.dp))
                        }
                        else{
                            Modifier.padding(2.dp)
                        }
                    })


            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style =
                        if (variant == VariantSelection.OUTLINE) {
                            MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = if (isSelected) 1f else 0.3f
                                )
                            )
                        } else {
                            MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (variant == VariantSelection.OUTLINE)
                                Modifier.padding(bottom = 8.dp)
                            else
                                Modifier.padding(vertical = 8.dp)
                        )
                )

            }
        }

    }
}