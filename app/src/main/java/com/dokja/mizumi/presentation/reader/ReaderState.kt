package com.dokja.mizumi.presentation.reader

//import com.dokja.mizumi.presentation.reader.components.LiveTranslationSettingData
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.dokja.mizumi.presentation.reader.components.TextToSpeechSettingData

class ReaderScreenState(
    val showReaderInfo: MutableState<Boolean>,
    val readerInfo: CurrentInfo,
    val settings: Settings,
    val showInvalidChapterDialog: MutableState<Boolean>
) {
    data class CurrentInfo(
        val bookTitle: State<String>,
        val chapterTitle: State<String>,
        val chapterCurrentNumber: State<Int>,
        val chapterPercentageProgress: State<Float>,
        val chaptersCount: State<Int>,
        val chapterUrl: State<String>,
    )

    data class Settings(
        val isTextSelectable: Boolean,
        val keepScreenOn: Boolean,
        val textToSpeech: TextToSpeechSettingData,
        val style: StyleSettingsData,
        val selectedSetting: MutableState<Type>,
    ) {
        data class StyleSettingsData(
//            val followSystem: State<Boolean>,
//            val currentTheme: State<Themes>,
            val textFont: String,
            val textSize: Float,
        )

        enum class Type {
            None, ChapterList, TextToSpeech, Style, More
        }
    }
}