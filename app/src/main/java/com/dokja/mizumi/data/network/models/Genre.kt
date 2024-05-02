package com.dokja.mizumi.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Genre(
    val id: Int,
    val label: String
)