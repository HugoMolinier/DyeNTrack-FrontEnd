package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.presentation.components.TopNavigationBar

@Composable
fun ParameterPage (modifier: Modifier = Modifier, navController: NavController) {





    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Paramètre", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() })

            LazyColumn(modifier= modifier.fillMaxWidth().padding(horizontal = 20.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {


            }
        }
    }
}