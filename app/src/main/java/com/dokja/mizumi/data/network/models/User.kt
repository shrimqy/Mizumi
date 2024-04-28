package com.dokja.mizumi.data.network.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userAuthToken")
    val userAuthToken: String?
)
