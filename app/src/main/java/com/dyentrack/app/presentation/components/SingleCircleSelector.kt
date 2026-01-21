package com.dyentrack.app.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun SingleCircleSelector(
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled:Boolean = true,
    size: Dp = 18.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.8f)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else secondaryTextColor,
                shape = CircleShape
            )
            .then(
                if (enabled && !isSelected) Modifier.clickable { onClick() }
                else Modifier
            )
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp) // taille du point intérieur
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            )
        }
    }
}