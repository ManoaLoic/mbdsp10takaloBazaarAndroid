package com.mustfaibra.roffu.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ExchangeService
import com.mustfaibra.roffu.models.Exchange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeService: ExchangeService
) : ViewModel() {

    private val _currentExchanges = MutableStateFlow<List<Exchange>>(emptyList())
    val currentExchanges: StateFlow<List<Exchange>> get() = _currentExchanges

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchCurrentExchanges() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = exchangeService.myCurrentExchange()
            if (response.isSuccessful) {
                _currentExchanges.value = response.body()?.data ?: emptyList()
            } else {
                // Handle error case
            }
            _isLoading.value = false
        }
    }
}
