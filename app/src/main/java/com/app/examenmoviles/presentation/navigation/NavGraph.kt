package com.app.examenmoviles.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.examenmoviles.presentation.screens.detail.DetailScreen
import com.app.examenmoviles.presentation.screens.home.HomeScreen

/**
 * Sealed class representing all screens in the app
 */
sealed class Screen(
    val route: String,
) {
    data object Home : Screen("home")

    data object Detail : Screen("country/{countryName}") {
        fun createRoute(countryName: String) = "country/$countryName"
    }
}

@Suppress("ktlint:standard:function-naming")
/**
 * Main navigation graph for the COVID-19 app
 */
@Composable
fun CovidNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        // Home Screen with two tabs (Country List and Search)
        composable(route = Screen.Home.route) {
            HomeScreen(
                onClick = { countryName ->
                    navController.navigate(Screen.Detail.createRoute(countryName))
                },
            )
        }

        // Detail Screen showing country COVID data
        composable(
            route = Screen.Detail.route,
            arguments =
                listOf(
                    navArgument("countryName") {
                        type = NavType.StringType
                    },
                ),
        ) { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: ""
            DetailScreen(
                countryName = countryName,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
