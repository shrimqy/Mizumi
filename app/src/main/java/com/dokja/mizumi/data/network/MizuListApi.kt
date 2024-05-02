package com.dokja.mizumi.data.network

import com.dokja.mizumi.data.network.models.BookResponse
import com.dokja.mizumi.data.network.models.UserBooks
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MizuListApi {
    @POST(Constants.LOGIN_URL)
    suspend fun login(@Body request: LoginRequest): String

    @GET(Constants.SEARCH_URL)
    suspend fun search(
        @Query("englishTitle") englishTitle: String
    ): Response<BookResponse>

    @GET(Constants.USER_BOOK_URL)
    suspend fun fetchUserBook(
        @Path("userID") userID: String,
        @Path("bookID") bookID: String
    ): UserBooks?

    @POST(Constants.USER_BOOK_BASE_URL)
    suspend fun createUserBook(@Body request: UserBookCreateRequest): UserBookCreateRequest?
}
