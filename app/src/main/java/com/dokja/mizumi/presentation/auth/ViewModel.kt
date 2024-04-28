package com.dokja.mizumi.presentation.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.data.network.LoginRequest
import com.dokja.mizumi.data.network.MizuListApi
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val localUserManager: LocalUserManager,
    private val api: MizuListApi
) : BaseViewModel() {

    fun login(username: String, password: String, onResult: (success: Boolean, token: String?) -> Unit) {
        val loginRequest = LoginRequest(username, password)
        viewModelScope.launch {
            Log.d("Login", loginRequest.toString())

            val response = try {
                api.login(loginRequest)
            } catch (e: Exception) {
                // Handle network exceptions
                Log.e("Login", "Error occurred: ${e.message}", e)
                onResult(false, null)
                return@launch
            }

            if (response.token != "") {
                val token = response.token
                Log.d("Login", token)
                localUserManager.saveUserToken(token)
                onResult(true, token)
            } else {
                Log.e("Login", "Empty token received")
                onResult(false, null)
            }
        }
    }
}