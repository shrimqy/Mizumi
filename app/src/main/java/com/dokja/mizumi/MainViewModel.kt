package com.dokja.mizumi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.domain.usecases.AppEntryUseCases
import com.dokja.mizumi.presentation.navgraph.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases,
    val localUserManager: LocalUserManager
): ViewModel() {

    var splashCondition by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf(Graph.MainScreenGraph)
        private set

    init {
        appEntryUseCases.readAppEntry().onEach { shouldStartFromHomeScreen ->
            if(shouldStartFromHomeScreen){
                startDestination = Graph.MainScreenGraph
            } else{
                startDestination = Graph.OnboardingGraph
            }
            delay(100)
            splashCondition = false
        }.launchIn(viewModelScope)

        localUserManager.readAppTheme().collect{

        }
    }
}