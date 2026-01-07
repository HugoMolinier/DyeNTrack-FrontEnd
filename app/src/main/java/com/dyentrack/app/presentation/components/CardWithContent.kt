package com.dyentrack.app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor

data class MenuItemData(
    val label: String,
    val onClick: () -> Unit,
    val style: TextStyle?=null,
    val canBeClick :Boolean = true,
    val color:Color=secondaryTextColor,
)

sealed class LeftIcon {
    data class Single(@DrawableRes val icon: Int) : LeftIcon()
    data class Multiple(@DrawableRes val icons: List<Int>) : LeftIcon()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardWithContent(
    modifier: Modifier = Modifier,
    text: String,
    subText: String,
    leftIcon: LeftIcon ? = null,
    badgeText: String? = null,
    componentRight: @Composable ((showMenu: () -> Unit) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    canExtend: Boolean = false,
    expanded: Boolean = false,
    onExpandChange: ((Boolean) -> Unit)? = null,
    menuOnLongClick: List<MenuItemData>? = null,
    childrenForExtend: @Composable (() -> Unit)? = null,
    subTextEmphasis: EmphasisType = EmphasisType.SECONDARY,
    hasLineShadowBetweenMainAndChildren: Boolean=false,
) {

    var showMenu by remember { mutableStateOf(false) }
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()            .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )

                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow
                    )
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,

            )

            ) {
            Column {
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

                            if(leftIcon!=null) {
                                Box(
                                    modifier = Modifier
                                        .height(58.dp)
                                        .width(58.dp)
                                        .background(
                                            color = TertiaOne.copy(
                                                alpha = 0.5f
                                            ),
                                            shape = RoundedCornerShape(5.dp)
                                        )


                                ) {
                                    when (leftIcon) {
                                        is LeftIcon.Single -> Image(
                                            painter = painterResource(id = leftIcon.icon),
                                            contentDescription = "Image de l'exercise",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )

                                        is LeftIcon.Multiple -> {
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp), // espace horizontal
                                                verticalArrangement = Arrangement.spacedBy(4.dp)    // espace vertical
                                            ) {
                                                leftIcon.icons.forEach { iconRes ->
                                                    Image(
                                                        painter = painterResource(id = iconRes),
                                                        contentDescription = "Image de l'exercice",
                                                        modifier = Modifier.size(24.dp), // adapter la taille selon besoin
                                                        contentScale = ContentScale.Fit
                                                    )
                                                }
                                            }
                                        }
                                    }

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
                                    color = if (subTextEmphasis == EmphasisType.SECONDARY) secondaryTextColor else MaterialTheme.colorScheme.primary
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
                                componentRight { showMenu = true }
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f))

                    ) {
                        childrenForExtend?.invoke()
                    }

                }


            }

        }
        DropdownMenu(
            expanded = showMenu, onDismissRequest = { showMenu = false },
            offset = DpOffset(x = (-66).dp, y = (-10).dp),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
        ) {

            menuOnLongClick?.forEach { menuItemAction ->
                DropdownMenuItem(
                    text = { Text(menuItemAction.label, style = menuItemAction.style?: MaterialTheme.typography.bodyMedium,color=menuItemAction.color ) },
                    onClick = {
                        if(menuItemAction.canBeClick) {
                            menuItemAction.onClick()
                            showMenu = false
                        }

                    },
                    enabled = menuItemAction.canBeClick
                )
            }
        }
    }
}