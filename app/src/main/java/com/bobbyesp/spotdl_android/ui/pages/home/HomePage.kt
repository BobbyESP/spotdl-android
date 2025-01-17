package com.bobbyesp.spotdl_android.ui.pages.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spotdl_android.R
import com.bobbyesp.spotdl_android.ui.StateHolder
import com.bobbyesp.spotdl_android.ui.components.ClearButton
import com.bobbyesp.spotdl_android.ui.components.SongCard
import com.bobbyesp.spotdl_android.ui.components.SongInfo
import kotlinx.coroutines.flow.update

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomePage(
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val taskState = StateHolder.taskState.collectAsStateWithLifecycle().value

    val existsSongInfo by remember(taskState.spotifySongInfo) { mutableStateOf(taskState.spotifySongInfo.isNotEmpty()) }

    val (text, setText) = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(), floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    StateHolder.mutableTaskState.update {
                        it.copy(url = text)
                    }
                    homeViewModel.downloadSong(text) { progress, _, line ->
                        //Divide the progress by 100 to get a value between 0 and 1
                        StateHolder.mutableTaskState.update {
                            it.copy(progress = progress, progressText = line)
                        }
                    }
                }) {
                Icon(
                    imageVector = Icons.Rounded.Download, contentDescription = "Download"
                )
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(id = R.string.app_description),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.alpha(0.8f)
                )
            }

            OutlinedTextField(
                value = text,
                isError = false,
                readOnly = taskState.isDownloading,
                onValueChange = { text -> setText(text) },
                label = { Text(stringResource(R.string.enter_url)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                trailingIcon = {
                    if (text.isNotEmpty()) ClearButton { setText("") }
                })
            AnimatedVisibility(visible = taskState.isDownloading) {
                //Linear progress indicator with the progress from the task state
                Column(
                    modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = taskState.progressText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                }
            }

            AnimatedVisibility(visible = existsSongInfo) {
                if (taskState.spotifySongInfo.isNotEmpty()) {
                    Column {
                        if (taskState.spotifySongInfo.size > 1) {
                            Text(
                                text = taskState.spotifySongInfo.size.toString() + " songs found. Showing the first one.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                            )
                        }
                        SongCard(
                            spotifySong = taskState.spotifySongInfo[0],
                            progress = taskState.progress,
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                            isLyrics = taskState.spotifySongInfo[0].lyrics?.isNotEmpty() == true,
                            isExplicit = taskState.spotifySongInfo[0].explicit,
                            onClick = { homeViewModel.openUrl(taskState.spotifySongInfo[0].url) })
                    }
                }
            }

            AnimatedVisibility(visible = existsSongInfo) {
                SongInfo(spotifySongs = taskState.spotifySongInfo)
            }

            Text(
                modifier = Modifier,
                text = "By the moment, the downloads directory is the Downloads folder of your phone into the spotdl subfolder.",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
            Button(
                onClick = { homeViewModel.openDownloadsFolder() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Open Downloads Folder")
            }
            Button(
                onClick = {
                    homeViewModel.updateSpotDLLibrary()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Try to update SpotDL")
            }
        }
    }
}

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start = this.calculateStartPadding(LayoutDirection.Ltr) + other.calculateStartPadding(
        LayoutDirection.Ltr
    ),
    top = this.calculateTopPadding() + other.calculateTopPadding(),
    end = this.calculateEndPadding(LayoutDirection.Ltr) + other.calculateEndPadding(LayoutDirection.Ltr),
    bottom = this.calculateBottomPadding() + other.calculateBottomPadding(),
)