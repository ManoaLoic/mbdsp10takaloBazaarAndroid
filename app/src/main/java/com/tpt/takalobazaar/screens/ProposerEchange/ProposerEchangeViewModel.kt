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

    fun proposeExchange(
        proposerObjects: List<Object>,
        receiverObjects: List<Object>,
        onSuccess: (String, Exchange?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true

            val proposerId = user.value?.id
            val receiverId = obj.value?.user?.id

            if (proposerId != null && receiverId != null) {
                val requestBody = ProposeExchangeRequest(
                    rcvUserId = receiverId,
                    rcvObjectId = receiverObjects.map { it.id },
                    prpObjectId = proposerObjects.map { it.id }
                )

                val response = exchangeService.proposerExchange(requestBody)

                if (response.isSuccessful) {
                    var responseExchange = response.body()
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

