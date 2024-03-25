package com.dokja.mizumi.presentation.reader.components.bottomBar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.presentation.reader.ReaderScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReaderScreenBottomBarDialogs(
    state: ReaderScreenState,
    chapters: List<Chapter>,
//    onTextFontChanged: (String) -> Unit,
//    onTextSizeChanged: (Float) -> Unit,
//    onSelectableTextChange: (Boolean) -> Unit,
//    onFollowSystem: (Boolean) -> Unit,
//    onThemeSelected: (Themes) -> Unit,
//    onKeepScreenOn: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(Modifier.padding(horizontal = 24.dp)) {
            AnimatedContent(targetState = state.settings.selectedSetting.value, label = "") { target ->
                when (target) {
                    ReaderScreenState.Settings.Type.ChapterList -> ChapterListBar(
                        chapters = chapters,
                        settings = state.settings
                    )
                    ReaderScreenState.Settings.Type.More -> MoreSettings(
//                        allowTextSelection = settings.isTextSelectable.value,
//                        onAllowTextSelectionChange = onSelectableTextChange,
//                        keepScreenOn = settings.keepScreenOn.value,
//                        onKeepScreenOn = onKeepScreenOn
                    )
                    ReaderScreenState.Settings.Type.None -> Unit
                    ReaderScreenState.Settings.Type.TextToSpeech -> TextToSpeech(
                        state = state,
                        settingsData = state.settings.textToSpeech
                    )
                    ReaderScreenState.Settings.Type.Style -> TODO()
                }
            }
        }
    }
}