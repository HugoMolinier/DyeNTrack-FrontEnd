package com.dyentrack.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyentrack.app.domain.model.enums.VariantType
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor


@Composable
fun SectionSelector(
    modifier: Modifier = Modifier,
    itemSection: List<MenuItemData>? = null,
    selectedIndex: Int? = 0,
    variant: VariantType = VariantType.FILLED
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (variant == VariantType.OUTLINED) {
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

                    .then(if (variant== VariantType.OUTLINED){
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
                        if (variant == VariantType.OUTLINED) {
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
                            if (variant == VariantType.OUTLINED)
                                Modifier.padding(bottom = 8.dp)
                            else
                                Modifier.padding(vertical = 8.dp)
                        )
                )

            }
        }

    }
}

@Composable
fun ObjectifBox(
    value: String ?= "0",
    objectif: String? = null,
    drawableTop: Int? = null,
    unit: String? = null,
    onClick: (() -> Unit)? = null,
    onClickObjectif: (() -> Unit)? = null,

    children: @Composable (() -> Unit)? = null,
) {

        Box(
            Modifier
                .fillMaxWidth().height(92.dp).width(150.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                ).clickable(onClick = { onClick?.invoke() }),
        ) {
            drawableTop?.let { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    modifier = Modifier.Companion.size(24.dp)
                        .align(Alignment.Companion.TopStart)
                        .offset(x = (-4).dp, y = (-4).dp)
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp), horizontalAlignment = Alignment.Companion.End
            ) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        value ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Companion.W600)
                    )
                    Text(
                        "/",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Companion.W600)
                    )
                    objectif?.let {
                        Box(
                            modifier = Modifier
                                .height(20.dp).fillMaxWidth(0.7f)
                                .background(TertiaOne, shape = MaterialTheme.shapes.small)
                                .clickable { onClickObjectif?.invoke() },
                            contentAlignment = Alignment.Companion.Center
                        ) {
                            Text(
                                objectif,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Companion.W600,
                                    color = secondaryTextColor,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
                unit?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = secondaryTextColor,
                            fontSize = 10.sp
                        ), modifier = Modifier.padding(top = 4.dp)
                    )
                }
                children?.invoke()
            }

        }



}