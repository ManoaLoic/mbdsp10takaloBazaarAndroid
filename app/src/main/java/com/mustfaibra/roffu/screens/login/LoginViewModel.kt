package com.mustfaibra.roffu.screens.login

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mustfaibra.roffu.api.AuthentificationService
import com.mustfaibra.roffu.api.RetrofitInstance
import com.mustfaibra.roffu.models.LoginRequest
import com.mustfaibra.roffu.models.LoginResponse
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.sealed.Error
import com.mustfaibra.roffu.sealed.UiState
import com.mustfaibra.roffu.services.SessionService
import com.mustfaibra.roffu.utils.UserPref
import com.mustfaibra.roffu.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class LoginViewModel @Inject constructor(
    private val context: Context,
    private val sessionService: SessionService
) : ViewModel() {
    private val authService: AuthentificationService = RetrofitInstance.createService(AuthentificationService::class.java)
    val uiState = mutableStateOf<UiState>(UiState.Idle)
    val emailOrPhone = mutableStateOf<String?>("")
    val password = mutableStateOf<String?>("")
    val errorMessage = mutableStateOf<String?>(null)

    fun updateEmailOrPhone(value: String?) {
        this.emailOrPhone.value = value
    }

    fun updatePassword(value: String?) {
        this.password.value = value
    }

    fun authenticateUser(
        emailOrPhone: String,
        password: String,
        onAuthenticated: () -> Unit,
        onAuthenticationFailed: () -> Unit,
    ) {
        if (emailOrPhone.isBlank() || password.isBlank()) onAuthenticationFailed()
        else {
            uiState.value = UiState.Loading
            viewModelScope.launch {
                val response = authService.login(LoginRequest(username = emailOrPhone, password = password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        uiState.value = UiState.Success
                        saveUserToPreferences(loginResponse.user)
                        sessionService.saveUser(loginResponse.user)
                        RetrofitInstance.setToken(loginResponse.user.token)
                        onAuthenticated()
                    } ?: run {
                        uiState.value = UiState.Error(error = Error.Unknown)
                        onAuthenticationFailed()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
                    val errorMessageText = errorJson["error"]?.asString ?: "Unknown error"
                    errorMessage.value = errorMessageText

                    uiState.value = UiState.Error(error = Error.Network)
                    onAuthenticationFailed()
                }
            }
        }
    }

    private suspend fun saveUserToPreferences(user: LoginUser) {
        val userJson = Gson().toJson(user)
        context.dataStore.edit { preferences ->
            preferences[UserPref.LOGGED_USER_INFO] = userJson
        }
    }
}
