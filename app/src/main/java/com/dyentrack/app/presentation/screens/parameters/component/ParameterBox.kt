package com.dyentrack.app.presentation.screens.parameters.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dyentrack.app.domain.model.ParameterIcon
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ParameterBox(items: List<ParameterItem>) {

    Box(
        Modifier
            .fillMaxWidth()

    ) {

        Column(
            Modifier.fillMaxWidth().padding()
        ) {
            items.forEachIndexed { index,(onClick, label, icon,composableRight) ->
                Box(
                    Modifier
                        .fillMaxWidth()        .then(
                            if (onClick != null) {
                                Modifier.clickable(
                                    onClick = onClick,
                                )
                            } else Modifier
                        )
                        .then(
                            if (index != items.lastIndex) {

                                Modifier.drawBehind {
                                    val yBottom = size.height - 0.5f
                                    drawLine(
                                        color = secondaryTextColor.copy(alpha = 0.15f),
                                        start = Offset(0f, yBottom),
                                        end = Offset(size.width, yBottom),
                                        strokeWidth = 1f
                                    )
                                }
                            }else Modifier
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().padding(horizontal = 8.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (icon) {
                                is ParameterIcon.Vector -> {
                                    Icon(
                                        imageVector = icon.icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(24.dp),
                                        tint = secondaryTextColor
                                    )
                                }

                                is ParameterIcon.Resource -> {
                                    Icon(
                                        painter = painterResource(icon.resId),
                                        contentDescription = label,
                                        modifier = Modifier.size(24.dp),
                                        tint = secondaryTextColor
                                    )
                                }


                                null -> null
                            }
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )
                        }
                        if (composableRight != null) {
                            composableRight()
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                "Aller au menu",
                                modifier = Modifier.size(20.dp),
                                tint = secondaryTextColor
                            )
                        }
                    }

                }
            }
        }
    }

}