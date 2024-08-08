package com.mustfaibra.roffu.screens.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.AuthentificationService
import com.mustfaibra.roffu.api.RetrofitInstance
import com.mustfaibra.roffu.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    public val sessionService: SessionService
) : ViewModel() {
    private val authService: AuthentificationService = RetrofitInstance.createService(AuthentificationService::class.java)
    val isLoggingOut = mutableStateOf(false)
    fun logOut(onLogoutSuccess: () -> Unit, onLogoutFailure: () -> Unit) {
        viewModelScope.launch {
            isLoggingOut.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    authService.logout()
                }
                sessionService.clearUser()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        onLogoutSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onLogoutFailure()
                    }
                }
            } catch (e: Exception) {
                sessionService.clearUser()
                withContext(Dispatchers.Main) {
                    onLogoutFailure()
                }
            } finally {
                isLoggingOut.value = false
            }
        }
    }
}
