package com.dokja.mizumi.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


object Constants {
    // Endpoints
    const val BASE_URL = "https://lit-back.onrender.com"
    const val LOGIN_URL = "/api/user/login"
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