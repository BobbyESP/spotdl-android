package com.bobbyesp.spotdl_android.ui.pages.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bobbyesp.spotdl_android.ui.StateHolder
import kotlinx.coroutines.flow.update
import kotlin.reflect.full.memberProperties

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
fun HomePage(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val taskState = StateHolder.taskState.collectAsStateWithLifecycle().value

    //get the progressText from the taskState but it can be updated in real time
    val outputText = remember { mutableStateOf(taskState.progressText) }

    val (text, setText) = remember { mutableStateOf("") }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colorScheme.background,

        ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = taskState.songInfo.isNotEmpty()) {
                    if (taskState.songInfo.isNotEmpty()) {
                        Surface( modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                            shadowElevation = 16.dp,onClick = { homeViewModel.openUrl(taskState.songInfo[0].url) }) {
                            Column(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = taskState.songInfo[0].cover_url,
                                    contentDescription = "Song cover",
                                    modifier = Modifier.size(192.dp)
                                )
                                Text(
                                    text = taskState.songInfo[0].name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    text = taskState.songInfo[0].artist,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .alpha(0.6f)
                                        .padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = { setText(it) },
                    label = { Text("Enter a Spotify URL") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        leadingIconColor = MaterialTheme.colorScheme.primary,
                        trailingIconColor = MaterialTheme.colorScheme.primary,
                        errorLabelColor = MaterialTheme.colorScheme.primary,
                        errorBorderColor = MaterialTheme.colorScheme.primary,
                        disabledBorderColor = MaterialTheme.colorScheme.primary,
                        disabledLabelColor = MaterialTheme.colorScheme.primary,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        placeholderColor = MaterialTheme.colorScheme.onBackground,
                        errorCursorColor = MaterialTheme.colorScheme.primary,
                        errorTrailingIconColor = MaterialTheme.colorScheme.primary,
                    )
                )
                AnimatedVisibility(visible = taskState.isDownloading) {
                    //Linear progress indicator with the progress from the task state
                    LinearProgressIndicator(
                        progress = taskState.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        //progress = taskState.progress, //Have to fix StreamProcessExtractor
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                //Text with the output of the download
                Text(taskState.progressText, modifier = Modifier.padding(4.dp))

                Button(onClick = {
                    homeViewModel.downloadSong(text) { progress, _, line ->
                        //Divide the progress by 100 to get a value between 0 and 1
                        StateHolder.mutableTaskState.update {
                            it.copy(progress = progress , progressText = line)
                        }
                    }
                }) {
                    Text(text = "Download")
                }
                Button(onClick = { homeViewModel.requestSongInfo(text) }) {
                    Text(text = "Request Song Info")
                }
                if (taskState.songInfo.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        taskState.songInfo.forEach {
                            val propertiesOfDto = it::class.memberProperties
                            for (property in propertiesOfDto) {
                                Text(
                                    text = "${property.name}: ${property.getter.call(it)}",
                                    modifier = Modifier.padding(4.dp)
                                )

                            }
                        }
                    }

                }


            }
        }
    }
}