package com.liflymark.normalschedule.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun SettingsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "mainSettings",
){
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = "mainSettings"){ SettingsMainPage(navController = navController) }
        composable(route = "courseCardSettings"){ SettingsCardPage(navController = navController) }
        composable(route="notificationSettings"){ NotificationSettingPage(navController = navController)}
        composable(route="logout"){ LogoutPage(navController = navController) }
    }
}