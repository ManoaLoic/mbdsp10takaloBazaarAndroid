package com.mustfaibra.roffu.screens.ficheobjet

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
class FicheObjetViewModel @Inject constructor(
    private val objectService: ObjectService
) : ViewModel() {

    private val _object = MutableStateFlow<Object?>(null)
    val obj: StateFlow<Object?> get() = _object

    fun fetchObjectById(objectId: Int) {
        viewModelScope.launch {
            val response = objectService.getObjectById(objectId)
            if (response.isSuccessful) {
                _object.value = response.body()
            } else {
                // Log error response
                println("Error fetching object: ${response.errorBody()?.string()}")
            }
        }
    }
}
