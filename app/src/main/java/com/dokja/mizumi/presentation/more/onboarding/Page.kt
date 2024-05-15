package com.dokja.mizumi.presentation.more.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.dokja.mizumi.R


data class Page(
    val title: String?,
    val description: String?,
    @DrawableRes val image: Int?,
    val content: @Composable () -> Unit = {}
)

val pages = listOf(
    Page(
        title = "Welcome!",
        description = "Let's set some things up first.",
        image = R.drawable.ic_logo,
    ),
    Page(
        title = "Disclaimer",
        description = "Our Reader does not endorse or support access to copyrighted content. Please use authorized platforms to obtain content for viewing and respect copyright laws.",
        image = R.drawable.disclaimer
    ),
    Page(
        title = null,
        description = null,
        image = null,
        content = { ThemeStep() }
    ),

)
