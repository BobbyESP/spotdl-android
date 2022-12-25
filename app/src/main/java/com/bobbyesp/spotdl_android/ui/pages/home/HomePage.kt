package com.bobbyesp.spotdl_android.ui.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.*

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomePage(
    homeViewModel: HomeViewModel = hiltViewModel()
){
    var url = ""
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ){
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(value = url, onValueChange = {url = it}, label = { Text("Enter Spotify URL") })
                Button(onClick = {homeViewModel.downloadSong(url) }) {
                 Text(text = "Download")
                }
            }
        }
    }
}