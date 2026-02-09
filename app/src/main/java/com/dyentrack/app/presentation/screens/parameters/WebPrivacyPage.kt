package com.dyentrack.app.presentation.screens.parameters

import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.presentation.components.TopNavigationBar

@Composable
fun WebPrivacyPage(url: String,    navController: NavController,) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            // Top navigation bar
            TopNavigationBar(
                text = stringResource(R.string.privacy),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() },
                rightSide = {
                    Icon(
                        Icons.Default.Close,
                        stringResource(R.string.back_to_menu),
                        modifier = Modifier.clickable { navController.navigate("home") }
                    )
                }
            )
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}