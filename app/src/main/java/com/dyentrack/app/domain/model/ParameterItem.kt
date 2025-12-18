package com.dyentrack.app.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ParameterItem(
    val onClick: () -> Unit,
    val label: String = "",
    val icon: ImageVector
)