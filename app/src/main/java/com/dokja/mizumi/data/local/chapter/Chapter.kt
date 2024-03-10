package com.dokja.mizumi.data.local.chapter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    val title: String?,
    @PrimaryKey val url: String,
    val bookUrl: String,
    val position: Int,
    val read: Boolean = false,
    val lastReadPosition: Int = 0,
    val lastReadOffset: Int = 0,
    val bookmark: Boolean = false
)