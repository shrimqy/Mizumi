package com.dokja.mizumi.data.local.library

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_library")
data class LibraryItem(
    val title: String,
    @PrimaryKey(autoGenerate = true)
    var libraryid: Int = 0,
    val url: String,
    val inLibrary: Boolean = false,
    val coverImageUrl: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val lastReadChapter: String? = null,
    val lastReadEpochTimeMilli: Long = 0,
)

//    fun fileExist(): Boolean {
//        val file = File(filePath)
//        return file.exists()
//    }
//
//    fun getFileSize(): String {
//        val file = File(filePath)
//        var bytes = file.length()
//        if (-1000 < bytes && bytes < 1000) {
//            return "$bytes B"
//        }
//        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
//        while (bytes <= -999950 || bytes >= 999950) {
//            bytes /= 1000
//            ci.next()
//        }
//        return java.lang.String.format(Locale.US, "%.1f %cB", bytes / 1000.0, ci.current())
//    }
//
//    fun deleteFile(): Boolean {
//        val file = File(filePath)
//        return try {
//            file.delete()
//        } catch (exc: IOException) {
//            false
//        }
//    }
