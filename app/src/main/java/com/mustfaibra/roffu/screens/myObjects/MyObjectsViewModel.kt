package com.mustfaibra.roffu.screens.myobjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.models.Object
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyObjectsViewModel @Inject constructor(
    private val objectService: ObjectService
) : ViewModel() {

    private val _userObjects = MutableStateFlow<List<Object>>(emptyList())
    val userObjects: StateFlow<List<Object>> get() = _userObjects

    fun getMyObjects() {
        viewModelScope.launch {
            val response = objectService.getUserObjects(1, emptyMap()) // Remplacez 1 par l'ID de l'utilisateur connecté
            if (response.isSuccessful) {
                _userObjects.value = response.body()?.data?.objects ?: emptyList()
            } else {
                // Gérer les erreurs
                println("Erreur lors de la récupération des objets : ${response.errorBody()?.string()}")
            }
        }
    }
}
