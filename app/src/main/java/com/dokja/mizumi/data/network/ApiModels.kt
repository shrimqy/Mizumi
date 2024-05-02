package com.dokja.mizumi.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


object Constants {
    // Endpoints
    const val BASE_URL = "https://lit-back.onrender.com"
    const val LOGIN_URL = "/api/user/login"
    const val SEARCH_URL = "/api/book/search"
    const val USER_BOOK_URL = "api/user-books/{userID}/{bookID}"
    const val USER_BOOK_BASE_URL = "api/user-books/"
}
@JsonClass(generateAdapter = true)
data class LoginRequest (
    @Json(name = "username")
    val username: String,
    @Json(name = "password")
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse (
    @Json(name = "token")
    var token: String,
)

@JsonClass(generateAdapter = true)
data class UserBookCreateRequest(
    @Json(name = "userID")
    var userId: String,
    @Json(name = "bookID")
    var bookID: String,
    @Json(name = "bookCategoryIds")
    var bookCategoryIds: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserBookCreateRequest

        return bookCategoryIds.contentEquals(other.bookCategoryIds)
    }

    override fun hashCode(): Int {
        return bookCategoryIds.contentHashCode()
    }
}