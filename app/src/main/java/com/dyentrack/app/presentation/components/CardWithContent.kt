package com.dyentrack.app.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.secondaryTextColor

enum class SubTextEmphasis { PRIMARY, SECONDARY }
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardWithContent (text: String, subText: String, leftIcon:ImageVector?=null,     badgeText: String? = null, iconRightForAction: ImageVector?=null,     onClick: (() -> Unit)? = null,
                     modifier: Modifier = Modifier,     canExtend: Boolean = false,
                     expanded: Boolean = false,
                     onExpandChange: ((Boolean) -> Unit)? = null,
                     menuOnLongClick: Map<String,()-> Unit>?=null,
                     childrenForExtend: @Composable (() -> Unit)? = null,
                     subTextEmphasis : SubTextEmphasis= SubTextEmphasis.SECONDARY,){

    var showMenu by remember() { mutableStateOf(false) }
    Box {
        Card(
            modifier = modifier.fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onClick?.invoke()
                        if (canExtend) onExpandChange?.invoke(!expanded)
                    },
                    onLongClick = {
                        if (!menuOnLongClick.isNullOrEmpty()) showMenu = true
                    }
                ).animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow
                    )
                ),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

            ) {
            Column() {
                Row(
                    modifier = modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 14.dp)
                        .height(40.dp), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                        if (leftIcon != null) {
                            Icon(imageVector = leftIcon, contentDescription = "icon")
                        } else {
                            Box(
                                modifier = Modifier.width(40.dp).height(40.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(5.dp)
                                    )


                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = subText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (subTextEmphasis == SubTextEmphasis.SECONDARY) secondaryTextColor else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (badgeText != null) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Text(badgeText)
                            }
                        }
                        if (iconRightForAction != null) {
                            Icon(imageVector = iconRightForAction, contentDescription = "icon")
                        }
                    }


                }
                if (canExtend && expanded) {
                    childrenForExtend?.invoke()

                }


            }

        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false },
            offset = DpOffset(x = (-66).dp, y = (-10).dp),
            shape = MaterialTheme.shapes.medium
        ) {

            menuOnLongClick?.forEach { (key, action) ->
                DropdownMenuItem(
                    text = { Text(key) },
                    onClick = {
                        action()
                        showMenu = false
                    })
            }
        }
    }
}