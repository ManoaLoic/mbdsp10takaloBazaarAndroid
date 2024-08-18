package com.tpt.takalobazaar.screens.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.api.UserService
import com.tpt.takalobazaar.models.CustomUser
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.models.ObjectListResponse
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

    private var pageNo: Int = 1
    private val pageSize: Int = 20
    private var currentSearchQuery: String = ""

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> get() = _hasMorePages

    fun fetchUserById(userId: Int) {
        viewModelScope.launch {
            try {
                val userResponse = userService.getUserProfile(userId)
                if (userResponse.isSuccessful) {
                    val user = userResponse.body()?.user
                    if (user != null) {
                        _userState.value = UserState.Success(user, emptyList())
                        loadUserObjects(userId)
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

    fun loadUserObjects(userId: Int, resetPage: Boolean = false) {
        if (_isLoading.value) return

        if (resetPage) {
            pageNo = 1
            _userState.value = (userState.value as? UserState.Success)?.copy(objects = emptyList()) ?: _userState.value
        }

        _isLoading.value = true
        viewModelScope.launch {
            val params = mutableMapOf<String, String>(
                "page" to pageNo.toString(),
                "limit" to pageSize.toString()
            )
            if (currentSearchQuery.isNotBlank()) {
                params["name"] = currentSearchQuery
            }
            val response = objectService.getUserObjects(userId, params)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val updatedObjects = (userState.value as? UserState.Success)?.objects.orEmpty() + data.objects
                    _userState.value = UserState.Success((userState.value as UserState.Success).user, updatedObjects)
                    _hasMorePages.value = data.currentPage < data.totalPages
                }
            } else {
                _hasMorePages.value = false
            }
            _isLoading.value = false
        }
    }

    fun loadNextPage(userId: Int) {
        if (_hasMorePages.value) {
            pageNo++
            loadUserObjects(userId)
        }
    }

    fun searchObjects(userId: Int, query: String) {
        currentSearchQuery = query
        loadUserObjects(userId, resetPage = true)
    }
}