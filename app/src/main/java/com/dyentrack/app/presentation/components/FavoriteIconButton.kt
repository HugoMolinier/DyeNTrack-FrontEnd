package com.dyentrack.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
        contentDescription = if (isFavorite) "Remove from favorite" else "Add to favorite",
        modifier = Modifier.clickable { onClick() }
    )
}
