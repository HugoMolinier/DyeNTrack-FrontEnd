package com.dyentrack.app.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dyentrack.app.presentation.components.CardWithContent

@Composable
fun HomePage(
    homeViewModel: HomeViewModel= hiltViewModel<HomeViewModel>()
) {
    val state by homeViewModel.uiState.collectAsState()
    LazyColumn(modifier = Modifier.padding(vertical = 50.dp)) {
        item {
            Column(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row {
                    Text("Scéances précédentes", Modifier.padding(bottom = 12.dp))
                }
                for (i in state.mapSeance.size - 1 downTo (state.mapSeance.size - 5).coerceAtLeast(0)) {
                    CardWithContent(modifier=Modifier,"nk",    state.mapSeance.keys.elementAt(i),        componentRight = {

                    },)

                }

            }
        }
    }
}

@Composable
private fun LastFiveSeance(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row {
            Text("Scéances précédentes", modifier.padding(bottom = 12.dp))
        }


    }
}

