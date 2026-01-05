package com.dyentrack.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ParameterIcon
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ParameterBox(items: List<ParameterItem>,
                 paddingValues: PaddingValues=PaddingValues(horizontal = 8.dp, vertical = 20.dp),
                 hasBorder:Boolean=true
) {


        Column(
            Modifier.fillMaxWidth()
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
                            if (index != items.lastIndex && hasBorder) {

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
                            .fillMaxWidth().padding(paddingValues),
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
                                stringResource(R.string.back_to_menu),
                                modifier = Modifier.size(20.dp),
                                tint = secondaryTextColor
                            )
                        }
                    }

                }
            }

    }

}