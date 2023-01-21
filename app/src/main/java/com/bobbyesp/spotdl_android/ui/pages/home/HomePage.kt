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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bobbyesp.spotdl_android.ui.StateHolder
import com.bobbyesp.spotdl_android.ui.components.SongCard
import kotlinx.coroutines.flow.update
import kotlin.reflect.full.memberProperties
import com.bobbyesp.spotdl_android.R
import com.bobbyesp.spotdl_android.ui.components.SongInfo

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
                        Column() {
                            if (taskState.songInfo.size > 1) {
                                Text(
                                    text = taskState.songInfo.size.toString() + " songs found. Showing the first one.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            SongCard(
                                song = taskState.songInfo[0],
                                progress = taskState.progress,
                                modifier = Modifier.padding(16.dp),
                                isLyrics = taskState.songInfo[0].lyrics?.isNotEmpty() ?: false,
                                isExplicit = taskState.songInfo[0].explicit,
                                onClick = { homeViewModel.openUrl(taskState.songInfo[0].url) }
                            )
                        }

                    }
                }
                OutlinedTextField(
                    value = text,
                    isError = false,
                    onValueChange = { setText(it) },
                    label = { Text(stringResource(R.string.enter_url)) },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    trailingIcon = {
                        if (text.isNotEmpty()) ClearButton { setText("") }
                    }
                )
                AnimatedVisibility(visible = taskState.isDownloading) {
                    //Linear progress indicator with the progress from the task state
                    LinearProgressIndicator(
                        progress = taskState.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                //Text with the output of the download
                Text(taskState.progressText, modifier = Modifier.padding(4.dp))

                Button(onClick = {
                    homeViewModel.downloadSong(text) { progress, _, line ->
                        //Divide the progress by 100 to get a value between 0 and 1
                        StateHolder.mutableTaskState.update {
                            it.copy(progress = progress, progressText = line)
                        }
                    }
                }) {
                    Text(text = "Download")
                }
                Button(onClick = { homeViewModel.requestSongInfo(text) }) {
                    Text(text = "Request Song Info")
                }
                AnimatedVisibility(visible = taskState.songInfo.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        SongInfo(songs = taskState.songInfo)
                    }
                }
            }
        }
    }
}

@Composable
fun ClearButton(function: () -> Unit) {
    IconButton(onClick = function) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
