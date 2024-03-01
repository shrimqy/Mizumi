package com.dokja.mizumi.data.local.chapter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChapterBody(
    @PrimaryKey val url: String,
    val body: String
)