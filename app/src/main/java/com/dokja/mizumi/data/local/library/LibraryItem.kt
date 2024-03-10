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
    val author: String? = "",
    val coverImageUrl: String = "",
    val description: String? = "",
    val completed: Boolean = false,
    val lastReadChapter: String? = null,
    val lastReadEpochTimeMilli: Long = 0,
)
