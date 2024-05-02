package com.dokja.mizumi.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookCategory(
    val id: Int,
    val name: String
)