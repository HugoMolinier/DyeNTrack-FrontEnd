package com.dyentrack.app.presentation.utils


fun Double.toCleanString(): String =
    toInt().takeIf { it.toDouble() == this }?.toString() ?: toString()
