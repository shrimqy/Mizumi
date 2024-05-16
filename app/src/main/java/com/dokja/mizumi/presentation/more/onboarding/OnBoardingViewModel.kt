package com.dokja.mizumi.presentation.more.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.data.manager.ThemePreferences
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.domain.ui.model.AppTheme
import com.dokja.mizumi.domain.usecases.AppEntryUseCases
import com.dokja.mizumi.presentation.model.ThemeMode
import com.dokja.mizumi.presentation.model.setAppCompatDelegateThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases,
    private val localUserManager: LocalUserManager
): ViewModel() {

    fun onEvent(event: OnBoardingEvent){
        when(event){
            is OnBoardingEvent.SaveAppEntry ->{
                saveAppEntry()
            }
        }
    }

    private fun saveAppEntry() {
        viewModelScope.launch {
            appEntryUseCases.saveAppEntry()
        }
    }

    private val _uiPreferences = MutableStateFlow(ThemePreferences(false, AppTheme.DEFAULT, ThemeMode.SYSTEM))
    val uiPreferences: MutableStateFlow<ThemePreferences> = _uiPreferences

    init {
        viewModelScope.launch {
            localUserManager.readAppTheme().collectLatest {
                _uiPreferences.value = it
            }
        }
    }

    fun updateAppTheme(themePreferences: ThemePreferences) {
        viewModelScope.launch{
            localUserManager.updateAppTheme(themePreferences)
        }
    }
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch{
            localUserManager.updateThemeMode(themeMode)

        }
    }
}