package com.tpt.takalobazaar.screens.ProposerEchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.ExchangeService
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.api.UserService
import com.tpt.takalobazaar.models.CreateResponse
import com.tpt.takalobazaar.models.CustomUser
import com.tpt.takalobazaar.models.ErrorResponse
import com.tpt.takalobazaar.models.Exchange
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.models.ProposeExchangeRequest
import com.tpt.takalobazaar.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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

    private val _targetUser = MutableStateFlow<CustomUser?>(null)
    val targetUser: StateFlow<CustomUser?> get() = _targetUser

    private val _sessionUser = MutableStateFlow<CustomUser?>(null)
    val sessionUser: StateFlow<CustomUser?> get() = _sessionUser

    var isLoading = MutableStateFlow(true)
        private set

    // Initialize without object
    fun initializeWithoutObject(userId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            fetchSessionUser()
            fetchUserById(userId)
            isLoading.value = false
        }
    }

    // Initialize with object
    fun initializeWithObject(userId: Int, objectId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            fetchSessionUser()
            fetchUserById(userId)
            fetchObjectById(objectId)
            isLoading.value = false
        }
    }

    private suspend fun fetchSessionUser() {
        val sessionId = sessionService.getUser()?.id
        sessionId?.let {
            val response = userService.getUserProfile(it)
            if (response.isSuccessful) {
                _sessionUser.value = response.body()?.user
            } else {
                println("Error fetching session user: ${response.errorBody()?.string()}")
            }
        }
    }

    private suspend fun fetchUserById(userId: Int) {
        val response = userService.getUserProfile(userId)
        if (response.isSuccessful) {
            _targetUser.value = response.body()?.user
        } else {
            println("Error fetching user: ${response.errorBody()?.string()}")
        }
    }

    private suspend fun fetchObjectById(objectId: Int) {
        val response = objectService.getObjectById(objectId)
        if (response.isSuccessful) {
            _object.value = response.body()
        } else {
            println("Error fetching object: ${response.errorBody()?.string()}")
        }
    }

    fun proposeExchange(
        proposerObjects: List<Object>,
        receiverObjects: List<Object>,
        onSuccess: (String, Exchange?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true

            val proposerId = sessionUser.value?.id
            val receiverId = targetUser.value?.id

            if (proposerId != null && receiverId != null) {
                val requestBody = ProposeExchangeRequest(
                    rcvUserId = receiverId,
                    rcvObjectId = receiverObjects.map { it.id },
                    prpObjectId = proposerObjects.map { it.id }
                )

                val response = exchangeService.proposerExchange(requestBody)

                if (response.isSuccessful) {
                    val responseExchange = response.body()
                    onSuccess("Échange proposé avec succès!", responseExchange?.exchange)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        try {
                            Json.decodeFromString<ErrorResponse>(ErrorResponse.serializer(), it).error
                        } catch (e: Exception) {
                            "Erreur lors de la proposition de l'échange."
                        }
                    } ?: "Erreur inconnue."

                    onError(errorMessage)
                }
            } else {
                onError("Utilisateur non trouvé.")
            }

            isLoading.value = false
        }
    }
}