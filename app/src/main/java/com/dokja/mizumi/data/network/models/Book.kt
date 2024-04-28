package com.dokja.mizumi.data.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Book(
    @SerializedName("id")
    val id: String,
    @SerializedName("englishTitle")
    val title: String,
    @SerializedName("authors")
    val authors: String,
    @SerializedName("description")
    val description: String,
)