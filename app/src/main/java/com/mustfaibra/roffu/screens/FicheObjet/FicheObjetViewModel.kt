package com.mustfaibra.roffu.screens.ficheobjet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FicheObjetViewModel @Inject constructor(
    private val objectService: ObjectService,
    private val sessionService: SessionService
) : ViewModel() {

    private val _objectState = MutableStateFlow<ObjectState>(ObjectState.Loading)
    val objectState: StateFlow<ObjectState> get() = _objectState

    fun fetchObjectById(objectId: Int) {
        viewModelScope.launch {
            try {
                val response = objectService.getObjectById(objectId)
                if (response.isSuccessful) {
                    _objectState.value = ObjectState.Success(response.body()!!)
                } else {
                    _objectState.value =
                        ObjectState.Error("Erreur lors de la récupération de l'objet.")
                }
            } catch (e: Exception) {
                _objectState.value = ObjectState.Error("Erreur: ${e.message}")
            }
        }
    }


    fun getCurrentUser(): Flow<LoginUser?> = flow {
        val user = runBlocking { sessionService.getUser() }
        emit(user)
    }

    fun repostObject(objectId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = objectService.repostObject(objectId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    onError("Erreur lors de la republication de l'objet : ${response.code()} ${response.message()} ${errorBody ?: ""}")
                }
            } catch (e: Exception) {
                onError("Erreur: ${e.message}")
            }
        }
    }


    fun removeObject(objectId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = objectService.removeObject(objectId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Erreur lors du retrait de l'objet.")
                }
            } catch (e: Exception) {
                onError("Erreur: ${e.message}")
            }
        }
    }


    sealed class ObjectState {
        object Loading : ObjectState()
        data class Success(val obj: Object) : ObjectState()
        data class Error(val message: String) : ObjectState()
    }
}

