package com.mustfaibra.roffu.screens.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.api.UserService
import com.mustfaibra.roffu.models.CustomUser
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.models.ObjectListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userService: UserService,
    private val objectService: ObjectService
) : ViewModel() {

    sealed class UserState {
        object Loading : UserState()
        data class Success(val user: CustomUser, val objects: List<Object>) : UserState()
        data class Error(val message: String) : UserState()
    }

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState

    fun fetchUserById(userId: Int) {
        viewModelScope.launch {
            try {
                val userResponse = userService.getUserProfile(userId)
                if (userResponse.isSuccessful) {
                    val user = userResponse.body()?.user
                    if (user != null) {
                        val objectsResponse = objectService.getUserObjects(userId, emptyMap())
                        val objects = objectsResponse.body()?.data?.objects ?: emptyList()
                        _userState.value = UserState.Success(user, objects)
                    } else {
                        _userState.value = UserState.Error("Utilisateur non trouvé")
                    }
                } else {
                    _userState.value = UserState.Error("Échec du chargement des données utilisateur")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Erreur : ${e.message}")
            }
        }
    }
}
