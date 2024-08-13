package com.tpt.takalobazaar.screens.editobject

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.models.ObjectRequest
import com.tpt.takalobazaar.models.Object
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
import android.graphics.BitmapFactory
import android.util.Base64
import com.tpt.takalobazaar.models.UpdateObject
import java.io.InputStream

@HiltViewModel
class EditObjectViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val objectService: ObjectService
) : ViewModel() {

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _object = MutableStateFlow<Object?>(null)
    val objectData: StateFlow<Object?> get() = _object

    fun fetchObjectById(objectId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = objectService.getObjectById(objectId)
                if (response.isSuccessful) {
                    _object.value = response.body()
                } else {
                    _toastMessage.value = "Erreur lors de la récupération de l'objet"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Exception: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateObject(
        objectId: Int,
        name: String,
        description: String,
        categoryId: Int,
        imageUri: Uri?,
        navController: NavHostController
    ) {
        viewModelScope.launch {
            // Initialize imageFile as null
            val imageFile: Pair<String, String>? = if (imageUri != null && !imageUri.toString().startsWith("http")) {
                uriToBase64(context, imageUri)
            } else {
                null
            }

            val objectRequest = UpdateObject(
                name = name,
                description = description,
                category_id = categoryId,
                image_file = imageFile?.first?.let { "data:image/${imageFile.second};base64,$it" }
            )

            _isLoading.value = true

            try {
                val response = objectService.updateObject(objectId, objectRequest)
                if (response.isSuccessful) {
                    _toastMessage.value = "Objet mis à jour avec succès"
                    navController.navigate("ficheobjet/$objectId") {
                        popUpTo("home") { inclusive = true }
                    }
                } else {
                    _toastMessage.value = "Erreur: ${response.message()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Exception: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun uriToBase64(context: Context, uri: Uri): Pair<String, String>? {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        val fileExtension = contentResolver.getType(uri)?.split("/")?.last() ?: return null
        val format = when (fileExtension) {
            "png" -> Bitmap.CompressFormat.PNG
            "webp" -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }
        bitmap.compress(format, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Pair(Base64.encodeToString(byteArray, Base64.NO_WRAP), fileExtension)
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
