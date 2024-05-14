package com.dokja.mizumi.presentation.more.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.dokja.mizumi.presentation.model.setAppCompatDelegateThemeMode
import com.dokja.mizumi.presentation.more.settings.widget.AppThemeModePreferenceWidget

class ThemeStep {
    @Composable
    fun Content() {
        val themeModePref = uiPreferences.themeMode()
        val themeMode by themeModePref.collectAsState()

        val appThemePref = uiPreferences.appTheme()
        val appTheme by appThemePref.collectAsState()

        val amoledPref = uiPreferences.themeDarkAmoled()
        val amoled by amoledPref.collectAsState()

        Column {
            AppThemeModePreferenceWidget(
                value = themeMode,
                onItemClick = {
                    themeModePref.set(it)
                    setAppCompatDelegateThemeMode(it)
                },
            )

            AppThemePreferenceWidget(
                value = appTheme,
                amoled = amoled,
                onItemClick = { appThemePref.set(it) },
            )
        }
    }
}
