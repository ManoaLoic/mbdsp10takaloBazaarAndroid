package com.tpt.takalobazaar.screens.ChangePassword

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.UserService
import com.tpt.takalobazaar.models.UpdateUserRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun changePassword(
        userId: Int,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (newPassword != confirmPassword) {
                onError("Les mots de passe ne correspondent pas")
                return@launch
            }

            _isLoading.value = true
            try {
                val updateUserRequest = UpdateUserRequest(
                    oldPassword = oldPassword,
                    password = newPassword,
                    confirmPassword = confirmPassword
                )

                val response = userService.updateUserProfile(userId, updateUserRequest)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    onError(errorMessage ?: "Une erreur est survenue")
                }
            } catch (e: Exception) {
                onError("Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        return try {
            // Assuming the errorBody is a JSON object with an "error" field
            val jsonObject = errorBody?.let { JSONObject(it) }
            jsonObject?.getString("error")
        } catch (e: JSONException) {
            null
        }
    }

}