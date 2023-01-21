package com.bobbyesp.spotdl_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.ui.PagesRouter
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel
import com.bobbyesp.spotdl_android.ui.theme.SpotdlandroidTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    private val REQUEST_WRITE_STORAGE = 112
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.baseContext

        //Request permission to write to storage
        val hasPermission = (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_STORAGE)
        }

        if(Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getFullAccessPermission = Intent()
                getFullAccessPermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getFullAccessPermission)
            }
        }
        setContent {
            val navController = rememberAnimatedNavController()
            SpotdlandroidTheme {
                PagesRouter(navController = navController , homeViewModel = homeViewModel)
            }

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}