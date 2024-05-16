package com.dokja.mizumi.presentation.model

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
}

fun setAppCompatDelegateThemeMode(themeMode: ThemeMode) {
    Log.d("themeMode", "$themeMode")
    AppCompatDelegate.setDefaultNightMode(
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        },
    )
}
