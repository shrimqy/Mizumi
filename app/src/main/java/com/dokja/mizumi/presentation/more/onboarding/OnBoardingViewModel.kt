package com.dokja.mizumi.presentation.more.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.data.manager.ThemePreferences
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.domain.ui.model.AppTheme
import com.dokja.mizumi.domain.usecases.AppEntryUseCases
import com.dokja.mizumi.presentation.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var uiPreferences by mutableStateOf(ThemePreferences(false, AppTheme.DEFAULT, ThemeMode.SYSTEM))
        private set
}