package com.dyentrack.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.ParameterIcon
import com.dyentrack.app.domain.model.ParameterItem
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun TopNavigationBar(
    text: String,
    leftIcon: ImageVector? = null,
    onClickLeft:(()->Unit)?=null,
    rightSide: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = colorScheme.primary,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(
            enabled = onClickLeft != null,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterStart).clip(RoundedCornerShape(5.dp)) ,

            onClick = { onClickLeft?.invoke()}
        ) {
            if (leftIcon != null) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Titre centré
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
                    style= MaterialTheme.typography.titleSmall


        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
        if (rightSide != null) {
                rightSide()

        } }
    }
}


