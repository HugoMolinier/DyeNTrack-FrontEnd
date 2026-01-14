package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.presentation.globalViewModel.AdViewModel
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdaptiveAdBanner(
    modifier: Modifier = Modifier,
    adViewModel: AdViewModel
) {
    val context = LocalContext.current
    var adSize by remember { mutableStateOf<AdSize?>(null) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthPx = constraints.maxWidth
        if (adSize == null && widthPx > 0) {
            adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context,
                (widthPx / LocalContext.current.resources.displayMetrics.density).toInt()
            )
        }

        adSize?.let { currentAdSize ->
            AndroidView(
                factory = { ctx ->
                    AdView(ctx).apply {
                        adUnitId = adViewModel.adUnitId
                        setAdSize(currentAdSize)
                        loadAd(adViewModel.adRequest)
                    }
                },
            )
        }
    }
}