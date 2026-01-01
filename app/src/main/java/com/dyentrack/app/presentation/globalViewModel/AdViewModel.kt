package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdRequest

class AdViewModel : ViewModel() {

    val adRequest: AdRequest by lazy {
        AdRequest.Builder().build()
    }

    val adUnitId = "ca-app-pub-3940256099942544/6300978111"
}
