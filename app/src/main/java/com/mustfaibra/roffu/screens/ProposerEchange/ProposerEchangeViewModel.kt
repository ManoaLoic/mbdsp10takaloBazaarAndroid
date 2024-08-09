package com.mustfaibra.roffu.screens.ProposerEchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ExchangeService
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.api.UserService
import com.mustfaibra.roffu.models.CustomUser
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.models.User
import com.mustfaibra.roffu.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProposerEchangeViewModel @Inject constructor(
    private val exchangeService: ExchangeService,
    public val objectService: ObjectService,
    private val sessionService: SessionService,
    private val userService: UserService
) : ViewModel() {

    private val _object = MutableStateFlow<Object?>(null)
    val obj: StateFlow<Object?> get() = _object

    private val _user = MutableStateFlow<CustomUser?>(null)
    val user: StateFlow<CustomUser?> get() = _user

    var isLoading = MutableStateFlow(true)
        private set

    init {
        // Initialize sessionId with the user in session
        viewModelScope.launch {
            val sessionId = sessionService.getUser()?.id
            sessionId?.let {
                fetchUserById(it)
            }
        }
    }

    private fun fetchUserById(userId: Int) {
        viewModelScope.launch {
            val response = userService.getUserProfile(userId)
            if (response.isSuccessful) {
                _user.value = response.body()?.user
            } else {
                // Log error response
                println("Error fetching user: ${response.errorBody()?.string()}")
            }
        }
    }

    fun fetchObjectById(objectId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            val response = objectService.getObjectById(objectId)
            if (response.isSuccessful) {
                _object.value = response.body()
            } else {
                // Log error response
                println("Error fetching object: ${response.errorBody()?.string()}")
            }
            isLoading.value = false
        }
    }

    fun proposeExchange(objectId: Int) {
        viewModelScope.launch {
//            val proposerId = user.value?.id
//            proposerId?.let {
//                exchangeService.proposeExchange(it, objectId)
//            }
        }
    }
}
