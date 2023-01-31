package com.bobbyesp.spotdl_android.ui.pages.home

import android.content.ClipboardManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spotdl_android.R
import com.bobbyesp.spotdl_android.ui.StateHolder
import com.bobbyesp.spotdl_android.ui.components.SongCard
import com.bobbyesp.spotdl_android.ui.components.SongInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
//IMPORTANT NOTE
//https://stackoverflow.com/questions/64951605/var-value-by-remember-mutablestateofdefault-produce-error-why
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomePage(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
    navigateToDownloads: () -> Unit,
) {
    val taskState = StateHolder.taskState.collectAsStateWithLifecycle().value

    val clipboardManager = LocalClipboardManager.current
    val (text, setText) = remember { mutableStateOf("") }

    //get 0 when the scroll is at the top and 1 when it's at the bottom
    val scrollState = rememberScrollState()

    val scrollPosition = remember { mutableStateOf(0f) }

    LaunchedEffect(scrollState.isScrollInProgress) {
        snapshotFlow { scrollState.value }
            .collect { scrollPosition.value = it.toFloat() }
    }

    LaunchedEffect(taskState.isDownloading) {
        Log.i("Scroll", scrollPosition.value.toString())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Scaffold(modifier = Modifier
            .fillMaxSize()
            .scrollable(scrollState, Orientation.Vertical), topBar = {
            TopAppBar(
                title = {},
                modifier = Modifier.padding(horizontal = 8.dp),
                navigationIcon = {
                    IconButton(onClick = { navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToDownloads() }) {
                        Icon(
                            imageVector = Icons.Outlined.Subscriptions,
                            contentDescription = stringResource(id = R.string.downloads_history)
                        )
                    }
                })
        }, floatingActionButton = {
            AnimatedVisibility(visible = true /*scrollPosition.value < 0.5f*/) {
                FABs(
                    modifier = with(receiver = Modifier.padding()) {
                        this.imePadding()
                    },
                    downloadCallback = {
                        homeViewModel.downloadSong(text) { progress, _, line ->
                            //Divide the progress by 100 to get a value between 0 and 1
                            StateHolder.mutableTaskState.update {
                                it.copy(progress = progress, progressText = line)
                            }
                        }
                    },
                    pasteCallback = { setText(clipboardManager.getText().toString()) },
                    requestInfoCallback = { homeViewModel.requestSongInfo(text) }
                )
            }
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.app_description),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.alpha(0.8f)
                        )
                        OutlinedTextField(
                            value = text,
                            isError = false,
                            onValueChange = { setText(it) },
                            label = { Text(stringResource(R.string.enter_url)) },
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            trailingIcon = {
                                if (text.isNotEmpty()) ClearButton { setText("") }
                            }
                        )
                        AnimatedVisibility(visible = taskState.isDownloading) {
                            //Linear progress indicator with the progress from the task state
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center
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
                        AnimatedVisibility(visible = taskState.songInfo.isNotEmpty()) {
                            if (taskState.songInfo.isNotEmpty()) {
                                Column {
                                    if (taskState.songInfo.size > 1) {
                                        Text(
                                            text = taskState.songInfo.size.toString() + " songs found. Showing the first one.",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 8.dp , bottom = 8.dp)
                                        )
                                    }
                                    SongCard(
                                        song = taskState.songInfo[0],
                                        progress = taskState.progress,
                                        modifier = Modifier.padding(top= 16.dp, bottom = 16.dp),
                                        isLyrics = taskState.songInfo[0].lyrics?.isNotEmpty()
                                            ?: false,
                                        isExplicit = taskState.songInfo[0].explicit,
                                        onClick = { homeViewModel.openUrl(taskState.songInfo[0].url) }
                                    )
                                }

                            }
                        }
                        AnimatedVisibility(visible = taskState.songInfo.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp)
                            ) {
                                SongInfo(songs = taskState.songInfo)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Text(
                            text = "By the moment, the downloads directory is the Downloads folder of your phone into the spotdl subfolder.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                        Button(
                            onClick = { homeViewModel.openDownloadsFolder() }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            Text(text = "Open Downloads Folder")
                        }
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

@Composable
fun FABs(
    modifier: Modifier = Modifier,
    downloadCallback: () -> Unit = {},
    pasteCallback: () -> Unit = {},
    requestInfoCallback: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(6.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = pasteCallback,
            contentPadding = PaddingValues(12.dp),
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.ContentPaste,
                        contentDescription = stringResource(R.string.pasteLink)
                    )
                    Text(
                        text = stringResource(R.string.pasteLink),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            shape = Shapes().small
        )
        Button(
            onClick = downloadCallback,
            contentPadding = PaddingValues(12.dp),
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Download,
                        contentDescription = stringResource(R.string.download)
                    )
                    Text(
                        text = stringResource(R.string.download),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }, modifier = Modifier.padding(vertical = 16.dp),
            shape = Shapes().small
        )

        Button(
            onClick = requestInfoCallback,
            contentPadding = PaddingValues(12.dp),
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.search_info)
                    )
                    Text(
                        text = stringResource(R.string.search_info),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }, shape = Shapes().small
        )
    }
}

@Composable
fun SettingsCheckbox(
    isCheckboxClicked :() -> Boolean,
    onCheckboxClick: () -> Unit,
    text: String,
){
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isCheckboxClicked(),
            onCheckedChange = { onCheckboxClick() },
            modifier = Modifier
                .padding(end = 4.dp)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 4.dp)
        )
    }

}

//get the text from the clipboard
fun ClipboardManager.getText(): Flow<CharSequence?> = callbackFlow {
    val listener = ClipboardManager.OnPrimaryClipChangedListener {
        trySend(getPrimaryClip()?.getItemAt(0)?.text).isSuccess
    }
    addPrimaryClipChangedListener(listener)
    awaitClose { removePrimaryClipChangedListener(listener) }
}
