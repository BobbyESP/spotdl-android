package com.bobbyesp.spotdl_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.ui.Navigator
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel
import com.bobbyesp.spotdl_android.ui.theme.SpotdlandroidTheme

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.baseContext
        setContent {
            val navController = rememberNavController()
            SpotdlandroidTheme {
                Navigator(navController = navController , homeViewModel = homeViewModel)
            }
        }
    }
}