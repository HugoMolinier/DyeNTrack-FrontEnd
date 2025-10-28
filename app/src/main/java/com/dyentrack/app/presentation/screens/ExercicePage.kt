package com.dyentrack.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.TopNavigationBar

@Composable
fun ExercisePage (modifier: Modifier = Modifier,navController: NavController,exercise: Exercise) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Détail de l’exercice", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = {  navController.popBackStack() })


            Row (modifier = modifier.fillMaxWidth().padding(start = 20.dp, top = 32.dp, end = 28.dp, bottom = 20.dp),horizontalArrangement = Arrangement.SpaceBetween){
                Text(exercise.nameFR)
                Icon(
                Icons.Default.StarBorder,"Add to favorite")
            }



            if(exercise.muscleInfos?.isNotEmpty() == true)
            InfoRow(label = "Muscle utilisé", value = exercise.muscleInfos.joinToString(),true)


        }
    }
}