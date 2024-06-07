package com.bobbyesp.spotdl_android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.library.domain.model.SpotifySong
import com.bobbyesp.spotdl_android.ui.common.AsyncImageImpl
import com.bobbyesp.spotdl_android.utils.GeneralUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongInfo(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    spotifySongs: List<SpotifySong>
) {
    //mutable state of expanded
    val expandedState = remember { mutableStateOf(expanded) }

    ElevatedCard(
        modifier = modifier,
        onClick = { expandedState.value = !expandedState.value },
        shape = MaterialTheme.shapes.small
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(end = 12.dp)
                        .padding(start = 8.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Song Info",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Click to expand",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.alpha(alpha = 0.8f)
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f, true)
                        )
                        val animatedDegree =
                            animateFloatAsState(targetValue = if (expandedState.value) 0f else -180f)

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.Bottom)
                        ) {
                            FilledTonalIconButton(
                                modifier = Modifier
                                    .padding()
                                    .size(24.dp),
                                onClick = { expandedState.value = !expandedState.value }) {
                                Icon(
                                    Icons.Outlined.ArrowDropDown,
                                    null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.rotate(animatedDegree.value)
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = expandedState.value) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        //adjust the size of the column to the quantity of items
                        .height(500.dp)
                ) {
                    LazyColumn {
                        item {
                            if (spotifySongs[0].song_list != null) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(4.dp),
                                    shape = MaterialTheme.shapes.small,
                                ) {
                                    Column(Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            spotifySongs[0].song_list?.cover_url?.let {
                                                AsyncImageImpl(
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .size(84.dp)
                                                        .aspectRatio(
                                                            1f,
                                                            matchHeightConstraintsFirst = true
                                                        )
                                                        .clip(MaterialTheme.shapes.small),
                                                    model = it,
                                                    contentDescription = "Song cover",
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }
                                            Column() {
                                                Text(
                                                    text = spotifySongs[0].song_list?.name ?: "Unknown",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = spotifySongs[0].song_list?.author_name
                                                        ?: "Unknown",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    modifier = Modifier.alpha(alpha = 0.8f)
                                                )
                                            }

                                        }
                                    }
                                }

                                Text(
                                    text = "Playlist with ${spotifySongs.size} songs",
                                    modifier = Modifier.padding(6.dp),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        items(
                            items = spotifySongs,
                        ) { song ->
                            if (spotifySongs[0].song_list == null) {
                                MetadataInfo(type = "Song name", value = song.name)
                                MetadataInfo(
                                    type = "Song artists",
                                    value = song.artists.toString()
                                )
                                MetadataInfo(type = "Song main artist", value = song.artist)
                                MetadataInfo(type = "Song album", value = song.album_name)
                                MetadataInfo(
                                    type = "Song album artist",
                                    value = song.album_artist
                                )
                                MetadataInfo(
                                    type = "Song genres",
                                    value = song.genres.toString()
                                )
                                MetadataInfo(
                                    type = "Song disc number",
                                    value = song.disc_number.toString()
                                )
                                MetadataInfo(
                                    type = "Song disc count",
                                    value = song.disc_count.toString()
                                )
                                MetadataInfo(
                                    type = "Song duration",
                                    value = song.duration.toString() + "//" + GeneralUtils.convertDuration(
                                        song.duration
                                    )
                                )
                                MetadataInfo(
                                    type = "Song year",
                                    value = song.year.toString()
                                )
                                MetadataInfo(type = "Song date", value = song.date)
                                MetadataInfo(
                                    type = "Song track number",
                                    value = song.track_number.toString()
                                )
                                MetadataInfo(type = "Song Spotify ID", value = song.song_id)
                                MetadataInfo(
                                    type = "Is Explicit",
                                    value = song.explicit.toString()
                                )
                                MetadataInfo(
                                    type = "Song publisher",
                                    value = song.publisher?: "Unknown"
                                )
                                MetadataInfo(type = "Song url", value = song.url)
                                MetadataInfo(
                                    type = "Song cover url",
                                    value = song.cover_url
                                )
                                MetadataInfo(type = "Song ISRC", value = song.isrc ?: "Unknown")
                                MetadataInfo(
                                    type = "Song copyright text",
                                    value = song.copyright_text ?: "Unknown"
                                )
                            } else {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImageImpl(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .aspectRatio(
                                                    1f,
                                                    matchHeightConstraintsFirst = true
                                                )
                                                .clip(MaterialTheme.shapes.small),
                                            model = song.cover_url,
                                            contentDescription = "Song cover",
                                            contentScale = ContentScale.Crop,
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 8.dp)
                                        ) {
                                            Text(
                                                text = song.name,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = song.artist,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.alpha(alpha = 0.8f)
                                            )

                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        MetadataInfo(type = "Song name", value = song.name)
                                        MetadataInfo(
                                            type = "Song artists",
                                            value = song.artists.toString()
                                        )
                                        MetadataInfo(type = "Song main artist", value = song.artist)
                                        MetadataInfo(type = "Song album", value = song.album_name)
                                        MetadataInfo(
                                            type = "Song album artist",
                                            value = song.album_artist
                                        )
                                        MetadataInfo(
                                            type = "Song genres",
                                            value = song.genres.toString()
                                        )
                                        MetadataInfo(
                                            type = "Song disc number",
                                            value = song.disc_number.toString()
                                        )
                                        MetadataInfo(
                                            type = "Song disc count",
                                            value = song.disc_count.toString()
                                        )
                                        MetadataInfo(
                                            type = "Song duration",
                                            value = song.duration.toString() + "//" + GeneralUtils.convertDuration(
                                                song.duration
                                            )
                                        )
                                        MetadataInfo(
                                            type = "Song year",
                                            value = song.year.toString()
                                        )
                                        MetadataInfo(type = "Song date", value = song.date)
                                        MetadataInfo(
                                            type = "Song track number",
                                            value = song.track_number.toString()
                                        )
                                        MetadataInfo(type = "Song Spotify ID", value = song.song_id)
                                        MetadataInfo(
                                            type = "Is Explicit",
                                            value = song.explicit.toString()
                                        )
                                        MetadataInfo(
                                            type = "Song publisher",
                                            value = song.publisher ?: "Unknown"
                                        )
                                        MetadataInfo(type = "Song url", value = song.url)
                                        MetadataInfo(
                                            type = "Song cover url",
                                            value = song.cover_url
                                        )
                                        MetadataInfo(type = "Song ISRC", value = song.isrc ?: "Unknown")
                                        MetadataInfo(
                                            type = "Song copyright text",
                                            value = song.copyright_text ?: "Unknown"
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetadataInfo(type: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = "$type: ",
            modifier = Modifier
                .padding(end = 4.dp)
                .weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f)
        )
    }
}
