package com.dokja.mizumi.repository

import com.dokja.mizumi.data.Response
import com.dokja.mizumi.data.local.AppDatabaseOperations
import com.dokja.mizumi.data.local.chapter.ChapterBody
import com.dokja.mizumi.data.local.chapter.ChapterBodyDao

class ChapterBodyRepository(
    private val chapterBodyDao: ChapterBodyDao,
    private val operations: AppDatabaseOperations,
    private val bookChaptersRepository: BookChaptersRepository,

    ) {
    suspend fun getAll() = chapterBodyDao.getAll()
    suspend fun insertReplace(chapterBodies: List<ChapterBody>) =
        chapterBodyDao.insertReplace(chapterBodies)

    suspend fun insertReplace(chapterBody: ChapterBody) =
        chapterBodyDao.insertReplace(chapterBody)

    suspend fun removeRows(chaptersUrl: List<String>) =
        chaptersUrl.chunked(500).forEach { chapterBodyDao.removeChapterRows(it) }

    suspend fun insertWithTitle(chapterBody: ChapterBody, title: String?) = operations.transaction {
        insertReplace(chapterBody)
        if (title != null)
            bookChaptersRepository.updateTitle(chapterBody.url, title)
    }

    suspend fun fetchBody(urlChapter: String, tryCache: Boolean = true): Response<String> {
        if (tryCache) chapterBodyDao.get(urlChapter)?.let {
            return@fetchBody Response.Success(it.body)
        }

        return Response.Error(
            """
            Unable to load chapter from url:
            $urlChapter
            
            Source is local but chapter content missing.
        """.trimIndent(), Exception()
        )
    }

}