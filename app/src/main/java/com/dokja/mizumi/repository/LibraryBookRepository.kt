package com.dokja.mizumi.repository

import android.content.Context
import android.net.Uri
import com.dokja.mizumi.data.local.AppDatabaseOperations
import com.dokja.mizumi.data.local.library.LibraryDao
import com.dokja.mizumi.data.local.library.LibraryItem
import com.dokja.mizumi.di.AppCoroutineScope
import com.dokja.mizumi.util.fileImporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryBookRepository(
    private val libraryDao: LibraryDao,
    private val operations: AppDatabaseOperations,
    @ApplicationContext val context: Context,
    private val appFileResolver: AppFileResolver,
    private val appCoroutineScope: AppCoroutineScope
) {
    val getBooksInLibraryWithContextFlow by lazy {
        libraryDao.getBooksInLibraryWithContextFlow()
    }

    fun getFlow(url: String) = libraryDao.getFlow(url)
    suspend fun insert(book: LibraryItem) = if (isValid(book)) libraryDao.insert(book) else Unit
    suspend fun insert(books: List<LibraryItem>) = libraryDao.insert(books.filter(::isValid))
    suspend fun insertReplace(books: List<LibraryItem>) =
        libraryDao.insertReplace(books.filter(::isValid))

    suspend fun remove(bookUrl: String) = libraryDao.remove(bookUrl)

    suspend fun remove(book: LibraryItem) = libraryDao.remove(book)
    suspend fun update(book: LibraryItem) = libraryDao.update(book)
    suspend fun updateLastReadEpochTimeMilli(bookUrl: String, lastReadEpochTimeMilli: Long) =
        libraryDao.updateLastReadEpochTimeMilli(bookUrl, lastReadEpochTimeMilli)

    suspend fun updateCover(bookUrl: String, coverUrl: String) =
        libraryDao.updateCover(bookUrl, coverUrl)


    suspend fun get(url: String) = libraryDao.get(url)
    suspend fun updateLastReadChapter(bookUrl: String, lastReadChapterUrl: String) =
        libraryDao.updateLastReadChapter(
            bookUrl = bookUrl,
            chapterUrl = lastReadChapterUrl
        )

    suspend fun getAllInLibrary() = libraryDao.getAllInLibrary()
    suspend fun existInLibrary(url: String) = libraryDao.existInLibrary(url)

    suspend fun toggleBookmark(
        bookUrl: String,
        bookTitle: String
    ): Boolean = operations.transaction {
        when (val book = get(bookUrl)) {
            null -> {
                insert(LibraryItem(title = bookTitle, url = bookUrl, inLibrary = true))
                true
            }
            else -> {
                update(book.copy(inLibrary = !book.inLibrary))
                !book.inLibrary
            }
        }
    }

    fun saveImageAsCover(imageUri: Uri, bookUrl: String) {
        appCoroutineScope.launch {
            val imageData = context.contentResolver.openInputStream(imageUri)
                ?.use { it.readBytes() } ?: return@launch
            val bookFolderName = appFileResolver.getLocalBookFolderName(
                bookUrl = bookUrl
            )
            val bookCoverFile = appFileResolver.getStorageBookCoverImageFile(
                bookFolderName = bookFolderName
            )
            fileImporter(targetFile = bookCoverFile, imageData = imageData)
            delay(timeMillis = 1_000)
            updateCover(bookUrl = bookUrl, coverUrl = appFileResolver.getLocalBookCoverPath())
        }
    }
}