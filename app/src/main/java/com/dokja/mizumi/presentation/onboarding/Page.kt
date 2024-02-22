package com.dokja.mizumi.presentation.onboarding

import androidx.annotation.DrawableRes
import com.dokja.mizumi.R


data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int?
)

val pages = listOf(
    Page(
        title = "WELCOME",
        description = "This is a FOSS Android application to read, track and browse Books/Novels.",
        image = null
    ),
    Page(
        title = "Disclaimer",
        description = "Our Reader does not endorse or support access to copyrighted content. Please use authorized platforms to obtain content for viewing and respect copyright laws.",
        image = R.drawable.disclaimer
    ),
    Page(
        title = "Connect to Litlog",
        description = "Browse through the catalog and explore new worlds to enjoy. Maintain and track your lists connecting to our site. Stay in the loop of new stuff as they come.",
        image = R.drawable.ic_tachi
    )
)
