package com.mustfaibra.roffu.screens.ajoutobjet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.models.Draft
import com.mustfaibra.roffu.models.ObjectRequest
import com.mustfaibra.roffu.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import android.util.Base64
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AjoutObjetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val objectService: ObjectService,
    private val database: AppDatabase
) : ViewModel() {

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _drafts = MutableStateFlow<List<Draft>>(emptyList())
    val drafts: StateFlow<List<Draft>> get() = _drafts

    fun createObject(
        name: String,
        description: String,
        categoryId: Int,
        imageUri: Uri?,
        navController: NavHostController
    ) {
        viewModelScope.launch {
            val (imageFile, fileExtension) = imageUri?.let { uriToBase64(context, it) } ?: Pair(null, null)
            if (imageFile == null || fileExtension == null) {
                _toastMessage.value = "Image required"
                return@launch
            }

            val objectRequest = ObjectRequest(
                name = name,
                description = description,
                category_id = categoryId,
                image_file = "data:image/$fileExtension;base64,$imageFile"
            )

            _isLoading.value = true

            try {
                val response = objectService.createObject(objectRequest)
                if (response.isSuccessful) {
                    val newObject = response.body()
                    _toastMessage.value = "Object créé avec succès"
                    newObject?.id?.let {
                        navController.navigate("ficheobjet/$it")
                        // Delete draft after successful submission
                        drafts.value.firstOrNull { draft ->
                            draft.name == name && draft.description == description && draft.categoryId == categoryId && draft.imageUri == imageUri.toString()
                        }?.let { draft ->
                            deleteDraft(draft.id)
                        }
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

    fun saveDraft(
        name: String,
        description: String,
        categoryId: Int,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            val draft = Draft(
                name = name,
                description = description,
                categoryId = categoryId,
                imageUri = imageUri.toString()
            )
            database.draftDao().insertDraft(draft)
            _toastMessage.value = "Brouillon enregistré"
        }
    }

    fun loadDrafts() {
        viewModelScope.launch {
            _drafts.value = database.draftDao().getAllDrafts()
        }
    }

    fun deleteDraft(draftId: Int) {
        viewModelScope.launch {
            database.draftDao().deleteDraft(draftId)
            loadDrafts()
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): Pair<String?, String?> {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        val fileExtension = context.contentResolver.getType(uri)?.split("/")?.last()
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
