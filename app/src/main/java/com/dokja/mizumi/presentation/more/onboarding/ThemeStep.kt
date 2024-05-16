package com.dokja.mizumi.presentation.more.onboarding

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokja.mizumi.data.manager.ThemePreferences
import com.dokja.mizumi.presentation.model.setAppCompatDelegateThemeMode
import com.dokja.mizumi.presentation.more.settings.widget.AppThemeModePreferenceWidget
import com.dokja.mizumi.presentation.more.settings.widget.AppThemePreferenceWidget

@Composable
    fun ThemeStep() {
        val viewModel: OnBoardingViewModel = hiltViewModel()
        val uiPreferences by viewModel.uiPreferences.collectAsState()
        val themeMode = uiPreferences.themeMode
        val appTheme = uiPreferences.appTheme
        val amoled = uiPreferences.isAmoled
        Column {
            AppThemeModePreferenceWidget(
                value = themeMode,
                onItemClick = {
                    viewModel.updateThemeMode(it)
                    setAppCompatDelegateThemeMode(it)
                },
            )

            AppThemePreferenceWidget(
                value = appTheme,
                amoled = amoled,
                onItemClick = {
                    Log.d("appTheme", "$it")
                    viewModel.updateAppTheme(ThemePreferences(isAmoled = amoled, appTheme = it, themeMode))
                },
            )
        }
    }

