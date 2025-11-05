package com.dyentrack.app.presentation.screens.exercise.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.TertiaOne

@Composable
fun SectionSelector(modifier: Modifier= Modifier){
    Row (modifier = Modifier.fillMaxWidth().background(TertiaOne, shape = RoundedCornerShape(8.dp)).padding(2.dp ) ){
        Box(
            modifier = modifier
                .fillMaxWidth().background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(6.dp))  .padding( vertical = 6.dp)
              .weight(1f)


        ) {
            Text("Explication",textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth() ,style= MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary) )

        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding( vertical = 8.dp).weight(1f),
        ) {
            Text("Statistique", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), style =  MaterialTheme.typography.bodyMedium)

        }
    }
}