package com.dokja.mizumi.data.local.tracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val libraryId: Int,
    val bookId: String,
    val bookCategory: Int,
    val userId: String,
    val chaptersRead: String?,
    val rating: String?,
    val completedAt: String?,
    val startedDate: String?
)

