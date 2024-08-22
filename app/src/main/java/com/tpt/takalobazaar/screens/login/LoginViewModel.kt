package com.tpt.takalobazaar.screens.login

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tpt.takalobazaar.api.AuthentificationService
import com.tpt.takalobazaar.api.RetrofitInstance
import com.tpt.takalobazaar.models.LoginRequest
import com.tpt.takalobazaar.models.LoginResponse
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.sealed.Error
import com.tpt.takalobazaar.sealed.UiState
import com.tpt.takalobazaar.services.SessionService
import com.tpt.takalobazaar.utils.UserPref
import com.tpt.takalobazaar.utils.dataStore
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
        fcmToken: String,
        serialNumber: String,
        onAuthenticated: () -> Unit,
        onAuthenticationFailed: () -> Unit,
    ) {
        if (emailOrPhone.isBlank() || password.isBlank()) onAuthenticationFailed()
        else {
            uiState.value = UiState.Loading
            viewModelScope.launch {
                val loginRequest = LoginRequest(
                    username = emailOrPhone,
                    password = password,
                    tokken = fcmToken,
                    serialNumber = serialNumber
                )
                val response = authService.login(loginRequest)
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

    fun getDeviceTokenAndSerialNumber(onTokenAndSerialReceived: (String, String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val androidId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) // Fetching Android ID
                Log.w("FCM", "Generated $token $androidId")
                onTokenAndSerialReceived(token, androidId)
            } else {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                onTokenAndSerialReceived("", "") // Handle failure gracefully
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
