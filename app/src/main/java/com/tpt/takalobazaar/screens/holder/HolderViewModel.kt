package com.tpt.takalobazaar.screens.holder

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolderViewModel @Inject constructor(
    private val sessionService: SessionService
) : ViewModel() {

    private val _user = MutableStateFlow<LoginUser?>(null)
    val user: StateFlow<LoginUser?> get() = _user

    init {
        observeUserSession()
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            sessionService.observeUser().distinctUntilChanged().collect { loginUser ->
                _user.value = loginUser
            }
        }
    }
}
