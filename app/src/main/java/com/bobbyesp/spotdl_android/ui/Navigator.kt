package com.bobbyesp.spotdl_android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bobbyesp.spotdl_android.ui.common.Route
import com.bobbyesp.spotdl_android.ui.common.Routes
import com.bobbyesp.spotdl_android.ui.pages.home.HomePage
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel

@Composable
fun Navigator(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            modifier = Modifier
                .fillMaxSize(),
            navController = navController,
            startDestination = Route.Home
        ) {
            composable<Route.Home> {
                HomePage(
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}