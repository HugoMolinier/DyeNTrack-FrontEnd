package com.dyentrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.dyentrack.app.domain.model.NavItem
import com.dyentrack.app.presentation.screens.HomePage
import com.dyentrack.app.presentation.screens.MainPage
import com.dyentrack.app.presentation.screens.seance.SeancePage
import com.dyentrack.app.presentation.theme.DyeNTrackTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DyeNTrackTheme() {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}


@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf(0) } // Pour suivre l'onglet sélectionné
    val navItemsList = listOf(
        NavItem("Home",Icons.Default.Home),
        NavItem("Séance", Icons.Default.Analytics),
        NavItem("Analyse", Icons.Default.Settings),
    )

    Scaffold (
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background ) {
                navItemsList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon, contentDescription = "icon"
                            )
                        },
                        label = { Text(item.label) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
        ) {

                when (selectedItem) {
                    0 -> HomePage()
                    1 -> SeancePage()
                    2 -> MainPage()
                }

        }
    }
}

