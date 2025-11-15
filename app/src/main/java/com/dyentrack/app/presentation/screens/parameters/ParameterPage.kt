package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.presentation.components.FavoriteIconButton
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.YouTubePlayerViewComposable
import com.dyentrack.app.presentation.screens.exercise.ExerciseViewModel
import com.dyentrack.app.presentation.screens.exercise.component.SectionSelector
import com.dyentrack.app.presentation.theme.secondaryTextColor

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