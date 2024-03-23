package com.dokja.mizumi.presentation.reader

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    state: ReaderScreenState,
    onKeepScreenOn: Boolean,
    onTextFontChanged: (String) -> Unit,
    onTextSizeChanged: (Float) -> Unit,
    onPressBack: () -> Unit,
    readerContent: @Composable (paddingValues: PaddingValues) -> Unit,
) {
    // Capture back action when viewing info
    BackHandler(enabled = state.showReaderInfo.value) {
        state.showReaderInfo.value = false
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = state.showReaderInfo.value,
                enter = expandVertically(initialHeight = { 0 }, expandFrom = Alignment.Top)
                        + fadeIn(),
                exit = shrinkVertically(targetHeight = { 0 }, shrinkTowards = Alignment.Top)
                        + fadeOut(),
            ) {
                Surface() {
                    Log.d("screen", "${state.readerInfo.chapterCurrentNumber.value}")
                    Column() {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ),
                            title = {
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = state.readerInfo.bookTitle.value,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.animateContentSize()
                                    )
                                    Text(
                                        text = state.readerInfo.chapterTitle.value,
                                        style = MaterialTheme.typography.titleSmall,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.animateContentSize()
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = onPressBack) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            },
                            actions = {
                            }
                        )
//                        Column(
//                            modifier = Modifier
//                                .padding(bottom = 8.dp)
//                                .padding(horizontal = 16.dp),
//                        ) {
//                            Text(
//                                text = stringResource(
//                                    id = R.string.show_chapter_number,
//                                    state.readerInfo.chapterCurrentNumber.value,
//                                    state.readerInfo.chaptersCount.value,
//                                ),
//                                style = MaterialTheme.typography.labelMedium,
//                            )
//                            Text(
//                                text = stringResource(
//                                    id = R.string.show_chapter_number,
//                                    state.readerInfo.chapterPercentageProgress.value
//                                ),
//                                style = MaterialTheme.typography.labelMedium,
//                            )
//                        }
                    }
                }
            }
        },
        content = readerContent,
        bottomBar = {

            val toggleOrSet = { type: ReaderScreenState.Settings.Type ->
                state.settings.selectedSetting.value = when (state.settings.selectedSetting.value) {
                    type -> ReaderScreenState.Settings.Type.None
                    else -> type
                }
            }
//            AnimatedVisibility(
//                visible = state.showReaderInfo.value,
//                enter = expandVertically(initialHeight = { 0 }) + fadeIn(),
//                exit = shrinkVertically(targetHeight = { 0 }) + fadeOut(),
//            ) {
//                Column {
//                    ReaderScreenBottomBarDialogs(
//                        settings = state.settings,
//                        onTextFontChanged = onTextFontChanged,
//                        onTextSizeChanged = onTextSizeChanged,
//                        onSelectableTextChange = onSelectableTextChange,
//                        onFollowSystem = onFollowSystem,
//                        onThemeSelected = onThemeSelected,
//                        onKeepScreenOn = onKeepScreenOn,
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    )
//                    BottomAppBar(
//                        modifier = Modifier.clip(
//                            RoundedCornerShape(
//                                topStart = 16.dp,
//                                topEnd = 16.dp
//                            )
//                        ),
//                        containerColor = MaterialTheme.colorApp.tintedSurface,
//                    ) {
//                        if (state.settings.liveTranslation.isAvailable) SettingIconItem(
//                            currentType = state.settings.selectedSetting.value,
//                            settingType = Type.LiveTranslation,
//                            onClick = toggleOrSet,
//                            icon = Icons.Outlined.Translate,
//                            textId = R.string.translator,
//                        )
//                        SettingIconItem(
//                            currentType = state.settings.selectedSetting.value,
//                            settingType = Type.TextToSpeech,
//                            onClick = toggleOrSet,
//                            icon = Icons.Filled.RecordVoiceOver,
//                            textId = R.string.voice_reader,
//                        )
//                        SettingIconItem(
//                            currentType = state.settings.selectedSetting.value,
//                            settingType = Type.Style,
//                            onClick = toggleOrSet,
//                            icon = Icons.Outlined.ColorLens,
//                            textId = R.string.style,
//                        )
//                        SettingIconItem(
//                            currentType = state.settings.selectedSetting.value,
//                            settingType = Type.More,
//                            onClick = toggleOrSet,
//                            icon = Icons.Outlined.MoreHoriz,
//                            textId = R.string.more,
//                        )
//                    }
//                }
//            }
        }
    )
}