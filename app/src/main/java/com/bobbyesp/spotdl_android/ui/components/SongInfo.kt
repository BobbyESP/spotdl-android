package com.bobbyesp.spotdl_android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.library.dto.Song
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongInfo(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    songs: List<Song>
){
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Click to expand", style = MaterialTheme.typography.bodyLarge)
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
                        items(
                            items = songs,
                        ) { song ->
                            val propertiesOfDto = song::class.memberProperties
                            for (property in propertiesOfDto) {
                                Text(
                                    text = "${property.name}: ${property.getter.call(song)}",
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