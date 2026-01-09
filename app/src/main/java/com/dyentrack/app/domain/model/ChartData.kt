package com.dyentrack.app.domain.model

data class ChartData(
    val labels: List<String>,
    val barValues: List<Float>,
    val lineValues: List<Float>,
    val showValueBar: Boolean = true,
    val showValueLine: Boolean = true
)