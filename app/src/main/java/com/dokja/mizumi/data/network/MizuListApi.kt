package com.dokja.mizumi.data.network

import retrofit2.http.Body
import retrofit2.http.POST

interface MizuListApi {
    @POST(Constants.LOGIN_URL)
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
