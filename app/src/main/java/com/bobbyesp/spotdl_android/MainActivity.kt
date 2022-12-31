package com.bobbyesp.spotdl_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import android.provider.Settings
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.ui.pages.home.HomePage
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel
import com.bobbyesp.spotdl_android.ui.theme.SpotdlandroidTheme


class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.baseContext
        if(Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getFullAcessPermission = Intent()
                getFullAcessPermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getFullAcessPermission)
            }
        }
        setContent {
            SpotdlandroidTheme {
                // A surface container using the 'background' color from the theme
                HomePage(homeViewModel)
            }

        }
    }
}