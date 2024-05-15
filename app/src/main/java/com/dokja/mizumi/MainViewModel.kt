package com.dokja.mizumi

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
import com.dokja.mizumi.presentation.navgraph.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases,
    private val localUserManager: LocalUserManager
): ViewModel() {

    var splashCondition by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf(Graph.MainScreenGraph)
        private set

    var uiPreferences by mutableStateOf(ThemePreferences(false, AppTheme.DEFAULT, ThemeMode.SYSTEM))
        private set

    init {
        appEntryUseCases.readAppEntry().onEach { shouldStartFromHomeScreen ->
            startDestination = if(shouldStartFromHomeScreen){
                Graph.MainScreenGraph
            } else{
                Graph.OnboardingGraph
            }
            delay(100)
            splashCondition = false
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            localUserManager.readAppTheme().collect{
                uiPreferences = it
            }
        }
    }
}