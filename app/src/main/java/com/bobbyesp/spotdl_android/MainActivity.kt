package com.bobbyesp.spotdl_android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.ui.pages.home.HomePage
import com.bobbyesp.spotdl_android.ui.pages.home.HomeViewModel
import com.bobbyesp.spotdl_android.ui.theme.SpotdlandroidTheme


class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.baseContext
        setContent {
            SpotdlandroidTheme {
                // A surface container using the 'background' color from the theme
                HomePage(homeViewModel)
            }

        }
    }
}
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpotdlandroidTheme {
        Greeting("Android")
    }
}