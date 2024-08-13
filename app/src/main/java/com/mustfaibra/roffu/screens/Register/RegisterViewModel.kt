package com.mustfaibra.roffu.screens.register

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mustfaibra.roffu.api.AuthentificationService
import com.mustfaibra.roffu.api.RetrofitInstance
import com.mustfaibra.roffu.models.ErrorResponse
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.models.RegisterRequest
import com.mustfaibra.roffu.services.SessionService
import com.mustfaibra.roffu.utils.UserPref
import com.mustfaibra.roffu.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val context: Context,
    private val authentificationService: AuthentificationService,
    private val sessionService: SessionService
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> get() = _uiState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun registerUser(
        user: RegisterRequest,
        onSuccess: () -> Unit,
        onToastRequested: (message: String, color: Color) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val response = authentificationService.register(user)
            if (response.isSuccessful) {
                _uiState.value = UiState.Success
                val responseUser = response.body()
                if (responseUser != null) {
                    saveUserToPreferences(responseUser)
                    sessionService.saveUser(responseUser)
                    RetrofitInstance.setToken(responseUser.token)
                    onSuccess()
                }
            } else {
                _uiState.value = UiState.Error
                // Extract error message and show a toast
                val errorResponse = response.errorBody()?.string()
                val errorMessage = errorResponse?.let {
                    Gson().fromJson(it, ErrorResponse::class.java).error
                } ?: "Erreur lors de l'inscription"
                onToastRequested(errorMessage, Color.Red)
            }
        }
    }

    private suspend fun saveUserToPreferences(user: LoginUser) {
        val userJson = Gson().toJson(user)
        context.dataStore.edit { preferences ->
            preferences[UserPref.LOGGED_USER_INFO] = userJson
        }
    }

    private fun extractErrorMessage(errorJson: String?): String? {
        return try {
            val jsonObject: JsonObject = JsonParser.parseString(errorJson).asJsonObject
            jsonObject.get("error").asString
        } catch (e: Exception) {
            null
        }
    }
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    object Error : UiState()
}
