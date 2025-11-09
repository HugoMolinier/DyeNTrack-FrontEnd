package com.dyentrack.app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.utils.bottomShadow

enum class SubTextEmphasis { PRIMARY, SECONDARY }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardWithContent(
    modifier: Modifier = Modifier,
    text: String,
    subText: String,
    @DrawableRes leftIcon: Int ? = null,
    badgeText: String? = null,
    componentRight: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    canExtend: Boolean = false,
    expanded: Boolean = false,
    onExpandChange: ((Boolean) -> Unit)? = null,
    menuOnLongClick: Map<String, () -> Unit>? = null,
    childrenForExtend: @Composable (() -> Unit)? = null,
    subTextEmphasis: SubTextEmphasis = SubTextEmphasis.SECONDARY,
    hasLineShadowBetweenMainAndChildren: Boolean=false,
) {

    var showMenu by remember() { mutableStateOf(false) }
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()

                .animateContentSize(
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
                    modifier = Modifier
                        .fillMaxWidth().combinedClickable(
                            onClick = {
                                onClick?.invoke()
                                if (canExtend) onExpandChange?.invoke(!expanded)
                            },
                            onLongClick = {
                                if (!menuOnLongClick.isNullOrEmpty()) showMenu = true
                            }
                        )
                      ,
                ) {
                    Row( horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,modifier = modifier.fillMaxWidth().padding(
                        start = 8.dp,
                        end = 20.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    )
                        .height(58.dp)) {


                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {

                            Box(
                                modifier = Modifier
                                    .height(58.dp)
                                    .width(58.dp)
                                    .background(
                                        color = if (leftIcon == null) TertiaOne else TertiaOne.copy(
                                            alpha = 0.5f
                                        ),
                                        shape = RoundedCornerShape(5.dp)
                                    )


                            ) {
                                if (leftIcon != null) {
                                    Image(
                                        painter = painterResource(id = leftIcon),
                                        contentDescription = "Image de l'exercise",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp)
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
                            if (componentRight != null) {
                                componentRight()
                            }
                        }


                    }
                }
                if (hasLineShadowBetweenMainAndChildren && expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .shadow(4.dp)
                    )
                }
                if (canExtend && expanded) {
                    childrenForExtend?.invoke()

                }


            }

        }
        DropdownMenu(
            expanded = showMenu, onDismissRequest = { showMenu = false },
            offset = DpOffset(x = (-66).dp, y = (-10).dp),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
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