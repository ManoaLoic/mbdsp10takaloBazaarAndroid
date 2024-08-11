package com.mustfaibra.roffu.screens.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.AuthentificationService
import com.mustfaibra.roffu.api.UserService
import com.mustfaibra.roffu.models.CustomUser
import com.mustfaibra.roffu.models.UpdateUserRequest
import com.mustfaibra.roffu.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    public val sessionService: SessionService,
    private val authService: AuthentificationService,
    private val userService: UserService
) : ViewModel() {
    val isLoggingOut = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val userProfile = mutableStateOf<CustomUser?>(null)
    val toastMessage = MutableStateFlow<String?>(null)

    var userId: Int? = null
        private set

    init {
        viewModelScope.launch {
            userId = sessionService.getUser()?.id
            fetchUserProfile()
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val userId = sessionService.getUser()?.id
                if (userId != null) {
                    val response = userService.getUserProfile(userId)
                    if (response.isSuccessful) {
                        userProfile.value = response.body()?.user
                    } else {
                        userProfile.value = null // Handle error case
                    }
                }
            } catch (e: Exception) {
                userProfile.value = null // Handle exception case
            } finally {
                isLoading.value = false
            }
        }
    }

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

    fun updateUserProfilePicture(uri: Uri, onUpdateSuccess: () -> Unit) {
        viewModelScope.launch {
            val (imageFile, fileExtension) = uriToBase64(sessionService.context, uri) ?: Pair(null, null)
            if (imageFile == null || fileExtension == null) {
                toastMessage.value = "Image required"
                return@launch
            }

            val updateUserRequest = UpdateUserRequest(
                image = "data:image/$fileExtension;base64,$imageFile"
            )

            isLoading.value = true

            try {
                val userId = sessionService.getUser()?.id
                if (userId != null) {
                    val response = userService.updateUserProfile(userId, updateUserRequest)
                    if (response.isSuccessful) {
                        userProfile.value = response.body()?.user
                        toastMessage.value = "Profile picture updated successfully"
                        onUpdateSuccess()
                    } else {
                        toastMessage.value = "Error: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                toastMessage.value = "Exception: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): Pair<String?, String?> {
        val contentResolver = context.contentResolver
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            // Handle exception if needed
        }

        var inputStream = contentResolver.openInputStream(uri)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        val fileExtension = contentResolver.getType(uri)?.split("/")?.last()

        // Get the orientation from the EXIF data
        var rotation = 0
        inputStream?.close()
        inputStream = contentResolver.openInputStream(uri)
        inputStream?.let {
            val exif = ExifInterface(it)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            rotation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
        inputStream?.close()

        // Rotate the bitmap if needed
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                matrix, true
            )
        }

        val outputStream = ByteArrayOutputStream()
        val format = when (fileExtension) {
            "png" -> Bitmap.CompressFormat.PNG
            "webp" -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }
        bitmap.compress(format, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Pair(Base64.encodeToString(byteArray, Base64.NO_WRAP), fileExtension)
    }
}
