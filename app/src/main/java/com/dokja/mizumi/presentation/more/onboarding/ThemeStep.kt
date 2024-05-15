package com.dokja.mizumi.presentation.more.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokja.mizumi.presentation.more.settings.widget.AppThemeModePreferenceWidget
import com.dokja.mizumi.presentation.more.settings.widget.AppThemePreferenceWidget

    @Composable
    fun ThemeStep() {
        val viewModel: OnBoardingViewModel = hiltViewModel()
        val themeMode = viewModel.uiPreferences.themeMode
        val appTheme = viewModel.uiPreferences.appTheme
        val amoled = viewModel.uiPreferences.isAmoled

        Column {
            AppThemeModePreferenceWidget(
                value = themeMode,
                onItemClick = {
//                    themeModePref.set(it)
//                    setAppCompatDelegateThemeMode(it)
                },
            )

            AppThemePreferenceWidget(
                value = appTheme,
                amoled = amoled,
                onItemClick = {
//                    appThemePref.set(it)
                              },
            )
        }
    }

