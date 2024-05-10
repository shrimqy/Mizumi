package com.dokja.mizumi.presentation.reader.manager

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.data.manager.ReaderPreferences
import com.dokja.mizumi.data.network.MizuListApi
import com.dokja.mizumi.di.AppCoroutineScope
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.presentation.reader.ChapterState
import com.dokja.mizumi.presentation.reader.ReaderChaptersLoader
import com.dokja.mizumi.presentation.reader.ReaderItem
import com.dokja.mizumi.presentation.reader.ReaderState
import com.dokja.mizumi.presentation.reader.ReadingChapterPosStats
import com.dokja.mizumi.presentation.reader.chapterReadPercentage
import com.dokja.mizumi.presentation.reader.components.ChaptersIsReadRoutine
import com.dokja.mizumi.presentation.reader.components.InitialPositionChapter
import com.dokja.mizumi.presentation.reader.components.ReaderTextToSpeech
import com.dokja.mizumi.repository.AppRepository
import com.dokja.mizumi.utils.Utterance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

class ReaderSession @Inject constructor(
    val bookUrl: String,
    initialChapterUrl: String,
    private val scope: CoroutineScope,
    private val appScope: AppCoroutineScope,
    private val appRepository: AppRepository,
    private val context: Context,
    val forceUpdateListViewState: suspend () -> Unit,
    val maintainLastVisiblePosition: suspend (suspend () -> Unit) -> Unit,
    val maintainStartPosition: suspend (suspend () -> Unit) -> Unit,
    val setInitialPosition: suspend (InitialPositionChapter) -> Unit,
    val showInvalidChapterDialog: suspend () -> Unit,
    private val localUserManager: LocalUserManager,
    private val api: MizuListApi,
) {
    private var chapterUrl: String = initialChapterUrl
    private val readRoutine = ChaptersIsReadRoutine(appRepository, api)
    private val orderedChapters = mutableListOf<Chapter>()

    var bookTitle: String? = null
    var bookCoverUrl: String? = null

    var currentChapter: ChapterState by Delegates.observable(
        ChapterState(
            chapterUrl = chapterUrl,
            chapterItemPosition = 0,
            offset = 0
        )
    ) { _, old, new ->
        chapterUrl = new.chapterUrl
        if (
            old.chapterUrl != new.chapterUrl &&
            savePositionMode.value == SavePositionMode.Reading
        ) {
            saveLastReadPositionState(new, old)
        }
    }


    private val readerPreferences: MutableStateFlow<ReaderPreferences> = MutableStateFlow(ReaderPreferences(16f, "serif", "", 1f, 1f))

    init {
        scope.launch {
            localUserManager.userReaderPreferences()
                .flowOn(Dispatchers.IO)
                .collectLatest { preferences ->
                    readerPreferences.value = preferences // Assign collected preferences to the MutableStateFlow
                }
        }
    }



    private enum class SavePositionMode { Reading, Speaking }

    private val savePositionMode = derivedStateOf<SavePositionMode> {
        if (readerTextToSpeech.isSpeaking.value) SavePositionMode.Speaking else
        SavePositionMode.Reading
    }

    val readingStats = mutableStateOf<ReadingChapterPosStats?>(null)
    val readingChapterProgressPercentage = derivedStateOf {
        readingStats.value?.chapterReadPercentage() ?: 0f
    }

    val speakerStats = derivedStateOf {
        val item = readerTextToSpeech.currentTextPlaying.value.itemPos
        readerChaptersLoader.getItemContext(
            chapterIndex = item.chapterIndex,
            chapterItemPosition = item.chapterItemPosition
        )
    }


    val readerChaptersLoader = ReaderChaptersLoader(
        appRepository = appRepository,
//        translatorTranslateOrNull = { readerLiveTranslation.translatorState?.translate?.invoke(it) },
//        translatorIsActive = { readerLiveTranslation.translatorState != null },
//        translatorSourceLanguageOrNull = { readerLiveTranslation.translatorState?.sourceLocale?.displayLanguage },
//        translatorTargetLanguageOrNull = { readerLiveTranslation.translatorState?.targetLocale?.displayLanguage },
        bookUrl = bookUrl,
        orderedChapters = orderedChapters,
        readerState = ReaderState.INITIAL_LOAD,
        forceUpdateListViewState = forceUpdateListViewState,
        maintainLastVisiblePosition = maintainLastVisiblePosition,
        maintainStartPosition = maintainStartPosition,
        setInitialPosition = setInitialPosition,
        showInvalidChapterDialog = showInvalidChapterDialog,
    )

    val items = readerChaptersLoader.getItems()

    val readerTextToSpeech = ReaderTextToSpeech(
        coroutineScope = scope,
        context = context,
        items = items,
        chapterLoadedFlow = readerChaptersLoader.chapterLoadedFlow,
        isChapterIndexLoaded = readerChaptersLoader::isChapterIndexLoaded,
        isChapterIndexTheLast = readerChaptersLoader::isChapterIndexTheLast,
        isChapterIndexValid = readerChaptersLoader::isChapterIndexValid,
        tryLoadPreviousChapter = readerChaptersLoader::tryLoadPrevious,
        loadNextChapter = readerChaptersLoader::tryLoadNext,
        getPreferredVoiceId = { readerPreferences.value.readerTTSVoiceId },
        setPreferredVoiceId = { readerPreferences.value.readerTTSVoiceId },
        getPreferredVoiceSpeed = { readerPreferences.value.readerTTSVoiceSpeed },
        setPreferredVoiceSpeed = { readerPreferences.value.readerTTSVoiceSpeed },
        getPreferredVoicePitch = { readerPreferences.value.readerTTSPitch },
        setPreferredVoicePitch = { readerPreferences.value.readerTTSPitch },
    )

    fun init() {
        initLoadData()
        scope.launch {
            appRepository.libraryBooks.updateLastReadEpochTimeMilli(
                bookUrl,
                System.currentTimeMillis()
            )
        }
        initReaderTTSObservers()
    }

    private fun initLoadData() {
        scope.launch {
            val book = async(Dispatchers.IO) { appRepository.libraryBooks.get(bookUrl) }
            val chapter = async(Dispatchers.IO) { appRepository.bookChapters.get(chapterUrl) }
//            val loadTranslator = async(Dispatchers.IO) { readerLiveTranslation.init() }
            val chaptersList = async(Dispatchers.Default) {
                orderedChapters.also { it.addAll(appRepository.bookChapters.chapters(bookUrl)) }
            }
            val chapterIndex = async(Dispatchers.Default) {
                chaptersList.await().indexOfFirst { it.url == chapterUrl }
            }
            chaptersList.await()
//            loadTranslator.await()
            bookCoverUrl = book.await()?.coverImageUrl
            bookTitle = book.await()?.title
            currentChapter = ChapterState(
                chapterUrl = chapterUrl,
                chapterItemPosition = chapter.await()?.lastReadPosition ?: 0,
                offset = chapter.await()?.lastReadOffset ?: 0,
            )
            // All data prepared! Let's load the current chapter
            readerChaptersLoader.tryLoadInitial(chapterIndex = chapterIndex.await())
        }
    }

    private fun initReaderTTSObservers() {
        scope.launch {
            readerTextToSpeech.reachedChapterEndFlowChapterIndex.collect { chapterIndex ->
                withContext(Dispatchers.Main.immediate) {
                    if (readerChaptersLoader.isLastChapter(chapterIndex)) return@withContext
                    val nextChapterIndex = chapterIndex + 1
                    val chapterItem = readerChaptersLoader.orderedChapters[nextChapterIndex]
                    if (readerChaptersLoader.loadedChapters.contains(chapterItem.url)) {
                        readerTextToSpeech.readChapterStartingFromStart(
                            chapterIndex = nextChapterIndex
                        )
                    } else launch {
                        readerChaptersLoader.tryLoadNext()
                        readerChaptersLoader.chapterLoadedFlow
                            .filter { it.type == ReaderChaptersLoader.ChapterLoaded.Type.Next }
                            .take(1)
                            .collect {
                                readerTextToSpeech.readChapterStartingFromStart(
                                    chapterIndex = nextChapterIndex
                                )
                            }
                    }
                }
            }
        }

//        scope.launch(Dispatchers.Main.immediate) {
//            snapshotFlow { readerTextToSpeech.isActive.value }
//                .filter { it }
//                .collectLatest {
//                    NarratorMediaControlsService.start(context)
//                }
//        }

        scope.launch(Dispatchers.Main.immediate) {
            readerTextToSpeech
                .currentReaderItem
                .filter { it.playState == Utterance.PlayState.PLAYING }
                .filter { savePositionMode.value == SavePositionMode.Speaking }
                .collect { saveLastReadPositionStateSpeaker(it.itemPos) }
        }

        scope.launch(Dispatchers.Main.immediate) {
            readerTextToSpeech
                .currentReaderItem
                .filter { it.playState == Utterance.PlayState.PLAYING }
                .filter { savePositionMode.value == SavePositionMode.Speaking }
                .collect {
                    val item = it.itemPos
                    if (item !is ReaderItem.ParagraphLocation) return@collect
                    when (item.location) {
                        ReaderItem.Location.FIRST -> markChapterStartAsSeen(chapterUrl = item.chapterUrl)
                        ReaderItem.Location.LAST -> markChapterEndAsSeen(chapterUrl = item.chapterUrl)
                        ReaderItem.Location.MIDDLE -> Unit
                    }
                }
        }
    }

    fun startSpeaker(itemIndex: Int) {
        val startingItem = items.getOrNull(itemIndex) ?: return
        readerTextToSpeech.start()
        scope.launch {
            readerTextToSpeech.readChapterStartingFromItemIndex(
                itemIndex = itemIndex,
                chapterIndex = startingItem.chapterIndex
            )
        }
    }

    fun close() {
        readerChaptersLoader.coroutineContext.cancelChildren()
        when (savePositionMode.value) {
            SavePositionMode.Reading -> saveLastReadPositionState(currentChapter)
            SavePositionMode.Speaking -> saveLastReadPositionStateSpeaker(
                item = readerTextToSpeech.currentTextPlaying.value.itemPos
            )
        }
        readerTextToSpeech.onClose()
        scope.coroutineContext.cancelChildren()
//        NarratorMediaControlsService.stop(context)
    }

    fun reloadReader() {
        readerChaptersLoader.reload()
        readerTextToSpeech.stop()
    }

    fun updateInfoViewTo(itemIndex: Int) {
        val stats = readerChaptersLoader.getItemContext(
            itemIndex = itemIndex,
            chapterUrl = chapterUrl
        ) ?: return
        readingStats.value = stats
    }

    fun markChapterStartAsSeen(chapterUrl: String) {
        readRoutine.setReadStart(chapterUrl = chapterUrl)
    }

    fun markChapterEndAsSeen(chapterUrl: String) {
        readRoutine.setReadEnd(chapterUrl = chapterUrl)
    }

    private fun saveLastReadPositionStateSpeaker(item: ReaderItem.Position) {
        saveLastReadPositionState(
            ChapterState(
                chapterUrl = item.chapterUrl,
                chapterItemPosition = item.chapterItemPosition,
                offset = 0
            )
        )
    }

    private fun saveLastReadPositionState(
        newChapter: ChapterState,
        oldChapter: ChapterState? = null,
    ) {
        saveBookLastReadPositionState(
            bookUrl = bookUrl,
            newChapter = newChapter,
            oldChapter = oldChapter,
            scope = appScope,
            appRepository = appRepository,
        )
    }
}

private fun saveBookLastReadPositionState(
    bookUrl: String,
    newChapter: ChapterState,
    oldChapter: ChapterState? = null,
    scope: AppCoroutineScope,
    appRepository: AppRepository,
) {
    scope.launch(Dispatchers.IO) {
        appRepository.withTransaction {
            appRepository.libraryBooks.updateLastReadChapter(
                bookUrl = bookUrl,
                lastReadChapterUrl = newChapter.chapterUrl
            )

            if (oldChapter?.chapterUrl != null) appRepository.bookChapters.updatePosition(
                chapterUrl = oldChapter.chapterUrl,
                lastReadPosition = oldChapter.chapterItemPosition,
                lastReadOffset = oldChapter.offset
            )

            appRepository.bookChapters.updatePosition(
                chapterUrl = newChapter.chapterUrl,
                lastReadPosition = newChapter.chapterItemPosition,
                lastReadOffset = newChapter.offset
            )
        }
    }
}
