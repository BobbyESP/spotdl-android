package com.bobbyesp.spotdl_android.ui.pages.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bobbyesp.spotdl_android.R
import com.bobbyesp.spotdl_android.ui.components.DrawerSheetSubtitle
import com.bobbyesp.spotdl_android.utils.PreferencesUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DownloadSettingsDialog(
    useDialog: Boolean = false,
    dialogState: Boolean = false,
    drawerState: ModalBottomSheetState,
    confirm: () -> Unit,
    hide: () -> Unit
) {
    val settings = PreferencesUtil

    var originalAudio by remember {
        mutableStateOf(settings.getValue(settings.ORIGINAL_AUDIO))
    }
    var lyrics by remember {
        mutableStateOf(settings.getValue(settings.LYRICS))
    }

    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val updatePreferences = {
        scope.launch {
            settings.updateValue(settings.ORIGINAL_AUDIO, originalAudio)
            settings.updateValue(settings.LYRICS, lyrics)
        }
    }

    val downloadButtonCallback = {
        updatePreferences()
        hide()
        confirm()
    }

    val sheetContent: @Composable () -> Unit = {
        Column {
            Text(
                text = stringResource(R.string.settings_before_download_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            DrawerSheetSubtitle(text = stringResource(id = R.string.general_settings))
        }

    }
}