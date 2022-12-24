package com.bobbyesp.spotdl_android.ui.pages

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.Job

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ApplicationEntries(
    homeViewModel: HomeViewModel
) {
    val navController = rememberAnimatedNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var updateJob: Job? = null
    val onBackPressed = { navController.popBackStack() }
}