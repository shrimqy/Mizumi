package com.dokja.mizumi.presentation.reader.components.bottomBar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.NavigateBefore
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.reader.ReaderScreenState
import com.dokja.mizumi.presentation.reader.components.TextToSpeechSettingData
import com.dokja.mizumi.presentation.utils.debouncedAction

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextToSpeech(
    state: ReaderScreenState,
    settingsData: TextToSpeechSettingData
) {
    AnimatedVisibility(
        visible = state.showReaderInfo.value && state.settings.selectedSetting.value == ReaderScreenState.Settings.Type.TextToSpeech,
        enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
    ) {
        Column {
//            AnimatedVisibility(visible = state.isLoadingChapter.value) {
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        strokeWidth = 6.dp,
//                        color = ColorAccent,
//                        modifier = Modifier.background(
//                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
//                            CircleShape
//                        )
//                    )
//                }
//            }
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Player voice parameters
//                    MySlider(
//                        value = state.voicePitch.value,
//                        valueRange = 0.1f..5f,
//                        onValueChange = state.setVoicePitch,
//                        text = stringResource(R.string.voice_pitch) + ": %.2f".format(state.voicePitch.value),
//                    )
//                    MySlider(
//                        value = state.voiceSpeed.value,
//                        valueRange = 0.1f..5f,
//                        onValueChange = state.setVoiceSpeed,
//                        text = stringResource(R.string.voice_speed) + ": %.2f".format(state.voiceSpeed.value),
//                    )

                    // Player settings buttons
                    FlowRow(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        AssistChip(
                            label = { Text(text = stringResource(id = R.string.start_here)) },
                            onClick = debouncedAction { settingsData.playFirstVisibleItem() },
                            leadingIcon = { Icon(Icons.Filled.CenterFocusWeak, null) },
                            colors = AssistChipDefaults.assistChipColors(
                                leadingIconContentColor = MaterialTheme.colorScheme.primary,
                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                        AssistChip(
                            label = { Text(text = stringResource(id = R.string.focus)) },
                            onClick = debouncedAction { settingsData.scrollToActiveItem() },
                            leadingIcon = { Icon(Icons.Filled.CenterFocusStrong, null) },
                            colors = AssistChipDefaults.assistChipColors(
                                leadingIconContentColor = MaterialTheme.colorScheme.primary,
                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
//                        AssistChip(
//                            label = { Text(text = stringResource(id = R.string.voices)) },
//                            onClick = { openVoicesDialog = !openVoicesDialog },
//                            leadingIcon = { Icon(Icons.Filled.RecordVoiceOver, null) },
//                            colors = AssistChipDefaults.assistChipColors(
//                                leadingIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                            ),
//                        )
//                        AssistChip(
//                            label = { Text(text = stringResource(R.string.saved_voices)) },
//                            onClick = {
//                                dropdownCustomSavedVoicesExpanded.let {
//                                    it.value = !it.value
//                                }
//                            },
//                            leadingIcon = { Icon(Icons.Filled.Bookmarks, null) },
//                            colors = AssistChipDefaults.assistChipColors(
//                                leadingIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                            ),
//                        )
                    }

                    // Player playback buttons
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val alpha by animateFloatAsState(
                            targetValue = if (settingsData.isThereActiveItem.value) 1f else 0.5f,
                            label = ""
                        )
                        IconButton(
                            onClick = debouncedAction(waitMillis = 1000) { settingsData.playPreviousChapter() },
                            enabled = settingsData.isThereActiveItem.value,
                            modifier = Modifier.alpha(alpha),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FastRewind,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                                tint = Color.White,
                            )
                        }
                        IconButton(
                            onClick = debouncedAction(waitMillis = 100) { settingsData.playPreviousItem() },
                            enabled = settingsData.isThereActiveItem.value,
                            modifier = Modifier.alpha(alpha),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.NavigateBefore,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                            )
                        }
                        IconButton(onClick = { settingsData.setPlaying(!settingsData.isPlaying.value) }) {
                            AnimatedContent(
                                targetState = settingsData.isPlaying.value,
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape), label = ""
                            ) { target ->
                                when (target) {
                                    true -> Icon(
                                        Icons.Rounded.Pause,
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                    false -> Icon(
                                        Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = debouncedAction(waitMillis = 100) { settingsData.playNextItem() },
                            enabled = settingsData.isThereActiveItem.value,
                            modifier = Modifier.alpha(alpha),
                        ) {
                            Icon(
                                Icons.Rounded.NavigateNext,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                            )
                        }
                        IconButton(
                            onClick = debouncedAction(waitMillis = 1000) { settingsData.playNextChapter() },
                            enabled = settingsData.isThereActiveItem.value,
                            modifier = Modifier.alpha(alpha),
                        ) {
                            Icon(
                                Icons.Rounded.FastForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                            )
                        }
                    }
                }
            }
        }
    }
}