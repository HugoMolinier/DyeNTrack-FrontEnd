package com.dyentrack.app

import android.content.Context
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.NavItem
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.presentation.globalViewModel.AppViewModel
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.globalViewModel.HealthConnectViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.screens.analyse.AnalysePage
import com.dyentrack.app.presentation.screens.exercise.ExercisePage
import com.dyentrack.app.presentation.screens.home.HomePage
import com.dyentrack.app.presentation.screens.parameters.ConnectedAppPage
import com.dyentrack.app.presentation.screens.parameters.ConnexionPage
import com.dyentrack.app.presentation.screens.parameters.InformationPage
import com.dyentrack.app.presentation.screens.parameters.LanguagePage
import com.dyentrack.app.presentation.screens.parameters.MesureUnitPage
import com.dyentrack.app.presentation.screens.parameters.ObjectifPage
import com.dyentrack.app.presentation.screens.parameters.ParameterPage
import com.dyentrack.app.presentation.screens.parameters.RegisterPage
import com.dyentrack.app.presentation.screens.seance.SeancePage
import com.dyentrack.app.presentation.theme.DyeNTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val healthViewModel: HealthConnectViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val appViewModel: AppViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleManager.updateContext(newBase)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        lifecycleScope.launch {
            val isConnected = authViewModel.authState.value.isConnected
            appViewModel.syncOnStart(isConnected)
        }
        healthViewModel.apply { }

        setContent {
            DyeNTrackTheme {
                MyApp()
            }
        }
    }

}



@Composable
fun MyApp() {
    val navController = rememberNavController()

    val navItemsList = listOf(
        NavItem(stringResource(R.string.home),Icons.Default.Home,route= "home"),
        NavItem(stringResource(R.string.workout), Icons.Default.Analytics,"seance"),
        NavItem(stringResource(R.string.analysis), Icons.Default.Settings,"analyse"),
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
                        selected = currentDestination?.hierarchy?.any {
                            it.route?.startsWith(item.route) == true
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(item.route) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSurface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            indicatorColor = Color.Transparent
                        )

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
            composable("home") { HomePage(navController=navController) }
            composable(
                route = "seance?dayKey={dayKey}&section={section}",
                arguments = listOf(
                    navArgument("dayKey") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("section") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val dayKey = backStackEntry.arguments?.getString("dayKey") // null si non fourni
                val section = backStackEntry.arguments?.getString("section")
                SeancePage(navController = navController, daySelected = dayKey,section=section)
            }
            composable("analyse") { AnalysePage(navController=navController) }
            composable("parametre") { ParameterPage(navController=navController) }
            composable("information") { InformationPage(navController=navController) }
            composable("connexion") { ConnexionPage(navController=navController)}
            composable("connectedApp") { ConnectedAppPage(navController=navController) }
            composable("objectif") { ObjectifPage(navController=navController) }
            composable("mesureUnit") { MesureUnitPage(navController=navController) }
            composable("language") { LanguagePage(navController=navController) }

            composable("inscription") { RegisterPage(navController=navController) }
                composable("exercise") { backStackEntry ->
                val exercise = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Exercise>("exercise")
                if (exercise != null) {
                    ExercisePage(navController = navController, exercise = exercise )
                }
            }

        }
    }
}

