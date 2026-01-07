package com.dyentrack.app.domain.model

import com.dyentrack.app.presentation.components.MenuItemData

data class ActiveMenu(
    val title: String,
    val description: String,
    val content: List<MenuItemData>
)