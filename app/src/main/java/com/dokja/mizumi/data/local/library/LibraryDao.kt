package com.dokja.mizumi.data.local.library

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dokja.mizumi.data.BookWithContext
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Query("SELECT * FROM book_library")
    fun getAllItems(): LiveData<List<LibraryItem>>

    @Query("SELECT * FROM book_library WHERE inLibrary == 1")
    suspend fun getAllInLibrary(): List<LibraryItem>

    @Query("SELECT * FROM book_library WHERE inLibrary == 1")
    fun booksInLibraryFlow(): Flow<List<LibraryItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: LibraryItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: List<LibraryItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(book: List<LibraryItem>)

    @Delete
    suspend fun remove(book: LibraryItem)

    @Query("DELETE FROM book_library WHERE book_library.url = :bookUrl")
    suspend fun remove(bookUrl: String)

    @Update
    suspend fun update(book: LibraryItem)

    @Query("UPDATE book_library SET lastReadChapter = :chapterUrl WHERE url == :bookUrl")
    suspend fun updateLastReadChapter(bookUrl: String, chapterUrl: String)

    @Query("UPDATE book_library SET lastReadEpochTimeMilli = :lastReadEpochTimeMilli WHERE url == :bookUrl")
    suspend fun updateLastReadEpochTimeMilli(bookUrl: String, lastReadEpochTimeMilli: Long)

    @Query("UPDATE book_library SET coverImageUrl = :coverUrl WHERE url == :bookUrl")
    suspend fun updateCover(bookUrl: String, coverUrl: String)


    @Query("SELECT * FROM book_library WHERE url = :url")
    suspend fun get(url: String): LibraryItem?

    @Query("SELECT * FROM book_library WHERE url = :url")
    fun getFlow(url: String): Flow<LibraryItem?>

    @Query("SELECT EXISTS(SELECT * FROM book_library WHERE url == :url AND inLibrary == 1)")
    suspend fun existInLibrary(url: String): Boolean

    @Query("""
        SELECT book_library.*, COUNT(Chapter.read) AS chaptersCount, SUM(Chapter.read) AS chaptersReadCount
        FROM book_library
        LEFT JOIN Chapter ON Chapter.bookUrl = book_library.url
        WHERE book_library.inLibrary == 1
        GROUP BY book_library.url
    """
    )
    fun getBooksInLibraryWithContextFlow(): Flow<List<BookWithContext>>


    @Query("DELETE FROM book_library WHERE inLibrary == 0")
    suspend fun removeAllNonLibraryRows()
}

