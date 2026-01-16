package com.dyentrack.app.domain.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class ParameterItem(
    val onClick: (() -> Unit)? = null,
    val label: String = "",
    val icon: ParameterIcon? = null,
    val composableRight: (@Composable () -> Unit)? = null,
    val sublabel: String? = null,
)

sealed class ParameterIcon {
    data class Vector(val icon: ImageVector) : ParameterIcon()
    data class Resource(@DrawableRes val resId: Int) : ParameterIcon()
}