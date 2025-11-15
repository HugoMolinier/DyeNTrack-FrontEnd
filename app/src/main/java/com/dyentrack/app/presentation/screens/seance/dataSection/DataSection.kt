package com.dyentrack.app.presentation.screens.seance.dataSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceDuJour
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel

@Composable
fun DataSection(    viewModel: SeanceViewModel = viewModel(),seanceDuJour: SeanceDuJour,lockInput:Boolean =false,day:String){



    Column(modifier = Modifier.padding(10.dp, 4.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
      NutritionBox(viewModel,seanceDuJour.nutritionTrack,lockInput,day)

        PhysioBox(viewModel,seanceDuJour.physioTrack,lockInput,day)


    }
}