package com.dokja.mizumi.presentation.reader.components.bottomBar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.presentation.reader.ReaderScreenState
import com.dokja.mizumi.presentation.reader.ReaderScreenState.Settings.Type

@Composable
fun BottomReaderBar(
    state: ReaderScreenState,
    chapters: List<Chapter>
//    onTextFontChanged: (String) -> Unit,
//    onTextSizeChanged: (Float) -> Unit,
) {
    val toggleOrSet = { type: Type ->
        state.settings.selectedSetting.value = when (state.settings.selectedSetting.value) {
            type -> Type.None
            else -> type
        }
    }
    AnimatedVisibility(
        visible = state.showReaderInfo.value,
        enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
    ) {
        Log.d("settings", "${state.settings.selectedSetting.value}")
        Column {
            ReaderScreenBottomBarDialogs(
                state = state,
                chapters = chapters,
//                onTextFontChanged = onTextFontChanged,
//                onTextSizeChanged = onTextSizeChanged,
//                onSelectableTextChange = onSelectableTextChange,
//                onFollowSystem = onFollowSystem,
//                onThemeSelected = onThemeSelected,
//                onKeepScreenOn = onKeepScreenOn,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            BottomAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp
                    )
                ),
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.97f),
                windowInsets = BottomAppBarDefaults.windowInsets
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { toggleOrSet(Type.ChapterList) }) {
                        Icon( Icons.Outlined.FormatListNumbered,null)
                    }
                    IconButton(onClick = { toggleOrSet(Type.TextToSpeech) }) {
                        Icon( Icons.AutoMirrored.Filled.VolumeUp,null)
                    }
                    IconButton(onClick = { toggleOrSet(Type.Style) }) {
                        Icon( Icons.Outlined.ColorLens,null)
                    }
                    IconButton(onClick = { toggleOrSet(Type.More) }) {
                        Icon( Icons.Outlined.Settings,null)
                    }
                }
            }
        }
    }
}
