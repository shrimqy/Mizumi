package com.dokja.mizumi.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResponse(
    @Json(name = "books")
    val books: List<Book>
)

@JsonClass(generateAdapter = true)
data class Book(
    @Json(name = "Description")
    val description: String?,
    @Json(name = "authors")
    val authors: String?,
    @Json(name = "chapters")
    val chapters: String?,
    @Json(name = "coverUrl")
    val coverUrl: String?,
    @Json(name = "englishTitle")
    val englishTitle: String?,
    @Json(name = "firstPublished")
    val firstPublished: String?,
    @Json(name = "genres")
    val genres: List<Genre>,
    @Json(name = "id")
    val id: String,
    @Json(name = "publicRating")
    val publicRating: Int,
    @Json(name = "ratingCount")
    val ratingCount: Int?,
    @Json(name = "romanizedTitle")
    val romanizedTitle: String?
)