package com.dyentrack.app.presentation.screens.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssistWalker
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor

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
                    modifier = Modifier.size(24.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-4).dp, y = (-4).dp)
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp), horizontalAlignment = Alignment.End
            ) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        value ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600)
                    )
                    Text(
                        "/",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600)
                    )
                    objectif?.let {
                        Box(
                            modifier = Modifier
                                .height(20.dp).fillMaxWidth(0.7f)
                                .background(TertiaOne, shape = MaterialTheme.shapes.small) .clickable { onClickObjectif?.invoke() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                objectif,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.W600,
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