package com.tpt.takalobazaar.screens.modifierprofil

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.UserService
import com.tpt.takalobazaar.models.UpdateUserRequest
import com.tpt.takalobazaar.models.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import androidx.navigation.NavHostController
import com.tpt.takalobazaar.sealed.Screen
import com.tpt.takalobazaar.services.SessionService

@HiltViewModel
class ModifierProfilViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userService: UserService,
) : ViewModel() {

    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> get() = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun loadUserProfile(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userService.getUserProfile(id)
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    println("RESPONSE : "+response)
                }
            } catch (e: Exception) {
                println("Erreur : "+e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        id: Int,
        username: String,
        email: String,
        first_name: String,
        last_name: String,
        gender : String,
        navController: NavHostController
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val updateUserRequest = UpdateUserRequest(
                    username = username,
                    email = email,
                    first_name = first_name,
                    last_name = last_name,
                    gender = gender
                )
                val response = userService.updateUserProfile(id, updateUserRequest)
                if (response.isSuccessful) {
                    _user.value = response.body()
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.ModifProfil.route) { inclusive = true }
                    }
                } else {
                    println("RESPONSE" + response)
                }
            } catch (e: Exception) {
                // Handle exception
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
