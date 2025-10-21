package com.dyentrack.app.utils

import android.content.res.Resources
import androidx.compose.runtime.Composable


@Composable
fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().getDisplayMetrics().density).toInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().getDisplayMetrics().density).toInt()
}