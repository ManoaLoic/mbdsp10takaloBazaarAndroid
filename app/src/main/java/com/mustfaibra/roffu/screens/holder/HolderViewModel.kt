package com.mustfaibra.roffu.screens.holder

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.models.CartItem
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.repositories.ProductsRepository
import com.mustfaibra.roffu.services.SessionService
import com.mustfaibra.roffu.utils.getStructuredCartItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolderViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
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

    val cartItems: MutableList<CartItem> = mutableStateListOf()
    val productsOnCartIds: MutableList<Int> = mutableStateListOf()
    val productsOnBookmarksIds: MutableList<Int> = mutableStateListOf()

}
