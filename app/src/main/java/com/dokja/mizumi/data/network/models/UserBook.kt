package com.dokja.mizumi.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserBooks(
    val userBooks: UserBook?
)
@JsonClass(generateAdapter = true)
data class UserBook(
    @Json(name = "id")
    val id: String,
    @Json(name = "bookID")
    val bookID: String,
    @Json(name = "userID")
    val userID: String,
    @Json(name = "chaptersRead")
    val chaptersRead: String?,
    @Json(name = "pagesRead")
    val pagesRead: String?,
    @Json(name = "completedAt")
    val completedAt: String?,
    @Json(name = "startedDate")
    val startedDate: String?,
    @Json(name = "bookCategory")
    val bookCategories: List<BookCategory>,
    @Json(name = "rating")
    val rating: String?,
    @Json(name = "rereads")
    val rereads: String?,
    @Json(name = "notes")
    val notes: String?,
    @Json(name = "lastUpdated")
    val lastUpdated: String?
)

