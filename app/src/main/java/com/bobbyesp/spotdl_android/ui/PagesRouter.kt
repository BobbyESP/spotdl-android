package com.bobbyesp.spotdl_android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bobbyesp.spotdl_android.ui.common.Routes
import com.bobbyesp.spotdl_android.ui.common.animatedComposable
import com.bobbyesp.spotdl_android.ui.pages.home.HomePage
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import kotlinx.coroutines.Job

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PagesRouter(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var updateJob: Job? = null
    val onBackPressed = { navController.popBackStack() }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedNavHost(
                modifier = Modifier
                    .fillMaxWidth(
                    )
                    .align(Alignment.Center),
                navController = navController,
                startDestination = Routes.DOWNLOADER
            ) {

                animatedComposable(Routes.DOWNLOADER) {
                    HomePage(
                        homeViewModel,
                        { navController.navigate(Routes.SETTINGS) },
                        { navController.navigate(Routes.DOWNLOADS_HISTORY) })
                }

                animatedComposable(Routes.SETTINGS) {
                    //TODO
                }

                animatedComposable(Routes.DOWNLOADS_HISTORY) {
                    //TODO
                }
            }
        }
    }

}