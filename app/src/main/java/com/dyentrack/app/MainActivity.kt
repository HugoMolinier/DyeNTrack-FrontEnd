package com.dyentrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dyentrack.app.domain.model.NavItem
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.presentation.screens.HomePage
import com.dyentrack.app.presentation.screens.MainPage
import com.dyentrack.app.presentation.screens.exercise.ExercisePage
import com.dyentrack.app.presentation.screens.seance.SeancePage
import com.dyentrack.app.presentation.theme.DyeNTrackTheme
import com.dyentrack.app.presentation.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainViewModel: AppViewModel by viewModels()
            DyeNTrackTheme() {
                MyApp(appViewModel = mainViewModel)
            }
        }
    }

}



@Composable
fun MyApp(appViewModel: AppViewModel) {
    val navController = rememberNavController()

    val navItemsList = listOf(
        NavItem("Home",Icons.Default.Home,route= "home"),
        NavItem("Séance", Icons.Default.Analytics,"seance"),
        NavItem("Analyse", Icons.Default.Settings,"analyse"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold (
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                navItemsList.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home",
            modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            composable("home") { HomePage() }
            composable("seance") { SeancePage(navController=navController, appViewModel = appViewModel) }
            composable("analyse") { MainPage(navController=navController) }
            composable("exercise") { backStackEntry ->
                val exercise = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Exercise>("exercise")
                if (exercise != null) {
                    ExercisePage(navController = navController, exercise = exercise , appViewModel = appViewModel)
                }
            }

        }
    }
}

