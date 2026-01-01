package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.presentation.globalViewModel.AdViewModel
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdaptiveAdBanner(
    modifier: Modifier = Modifier,
    maxHeight: Dp = 120.dp,
    adViewModel: AdViewModel
) {

    AndroidView(
        modifier = modifier
            .heightIn(max = maxHeight),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.FULL_BANNER)
                adUnitId = adViewModel.adUnitId
                loadAd(adViewModel.adRequest)
            }
        }
    )
}
