package com.dokja.mizumi.presentation.reader.components.bottomBar

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.presentation.reader.ReaderActivity
import com.dokja.mizumi.presentation.reader.ReaderScreenState
import com.dokja.mizumi.presentation.utils.ReadItemAlpha
import com.dokja.mizumi.presentation.utils.SecondaryItemAlpha

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChapterListBar(
    chapters: List<Chapter>,
    settings: ReaderScreenState.Settings
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { isSheetOpen = false; settings.selectedSetting.value = ReaderScreenState.Settings.Type.None  },
        windowInsets = WindowInsets(0.dp),
        modifier = Modifier,
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            items(
                items = chapters,
                key = { "_" + it.url},
                contentType = { 4 }
            ) {
                val textAlpha = if (it.read) ReadItemAlpha else 1f
                val textSubtitleAlpha = if (it.read) ReadItemAlpha else SecondaryItemAlpha
                var textHeight by remember { mutableIntStateOf(0) }
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().combinedClickable(
                        onClick = {
                            val intent = Intent(context, ReaderActivity::class.java).apply {
                            putExtra("bookUrl",  it.bookUrl)
                            putExtra("chapterUrl", it.url)
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                            context.startActivity(intent) }
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = it.title!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalContentColor.current.copy(alpha = textAlpha),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textHeight = it.size.height },
                        )
                    }
                }

            }
        }
    }
}