package com.dokja.mizumi.presentation.reader.components

import android.util.Log
import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.data.network.MizuListApi
import com.dokja.mizumi.data.network.UserBookUpdateRequest
import com.dokja.mizumi.presentation.reader.ChapterUrl
import com.dokja.mizumi.repository.AppRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ChaptersIsReadRoutine(
    val appRepository: AppRepository,
    val api: MizuListApi,
    private val scope: CoroutineScope = CoroutineScope(
        Dispatchers.IO + SupervisorJob() + CoroutineName("ChapterIsReadRoutine")
    )
) {
    fun setReadStart(chapterUrl: String) = checkLoadStatus(chapterUrl) { it.copy(startSeen = true) }
    fun setReadEnd(chapterUrl: String) = checkLoadStatus(chapterUrl) {
        it.copy(endSeen = true)

    }

    private fun getLastReadChapterNumber(chapterTitle: String?): Int? {
        return chapterTitle?.let { title ->
            val trimmedTitle = title.trim().takeWhile { it != ':' }
            val splitTitle = trimmedTitle.split(" ")
            val regex = Regex("\\d+")
            val potentialNumber = splitTitle.firstOrNull { it.matches(regex) }?.toIntOrNull()
            potentialNumber
        }
    }

    private fun getLastReadChapterIndex(chapters: List<Chapter>): Int? {
        // Find the last read chapter from the bottom
        val lastReadChapter = chapters.lastOrNull {it.read}
        // If a read chapter is found, return its chapter number, otherwise -1
        return if (lastReadChapter != null) {
            chapters.indexOf(lastReadChapter) + 1
        } else {
            null
        }
    }

    private data class ChapterReadStatus(val startSeen: Boolean, val endSeen: Boolean)

    private val chapterRead = mutableMapOf<ChapterUrl, ChapterReadStatus>()

    private fun checkLoadStatus(chapterUrl: String, fn: (ChapterReadStatus) -> ChapterReadStatus) =
        scope.launch {
            val chapter = appRepository.bookChapters.get(chapterUrl) ?: return@launch
            val oldStatus = chapterRead.getOrPut(chapterUrl) {
                when (chapter.read) {
                    true -> ChapterReadStatus(startSeen = true, endSeen = true)
                    false -> ChapterReadStatus(startSeen = false, endSeen = false)
                }
            }

            if (oldStatus.startSeen && oldStatus.endSeen) return@launch

            val newStatus = fn(oldStatus)
            if (newStatus.startSeen && newStatus.endSeen) {
                appRepository.bookChapters.setAsRead(chapterUrl = chapterUrl, read = true)
                val book = appRepository.libraryBooks.get(chapter.bookUrl)
                val chapterReadNumber = getLastReadChapterNumber(chapter.title) ?: getLastReadChapterIndex(appRepository.bookChapters.chapters(chapter.bookUrl))
                val track = book?.let { appRepository.tracker.getTrack(it.libraryid) }
                if (track != null) {
                    appRepository.tracker.updateChapterRead(libraryId = book.libraryid, bookCategory = 2, chaptersRead = chapterReadNumber.toString())
                    val userBookUpdateRequest = UserBookUpdateRequest(track.userId, track.bookId,
                        intArrayOf(1,2), rating = null, chapterReadNumber.toString(), startedDate = null, completedAt = null)
                    try {
                        val response = api.updateUserBook(
                            userID = track.userId,
                            bookID = track.bookId,
                            userBookUpdateRequest
                        )
                        Log.d("UserBookUpdateResponse", "$response")
                    } catch (e: Exception) {
                        Log.e("UserBookUpdateResponse", "Error occurred: ${e.message}", e)
                    }
                }
            }

            chapterRead[chapterUrl] = newStatus

        }



}