package com.mustfaibra.roffu.screens.FicheEchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ExchangeService
import com.mustfaibra.roffu.models.Exchange
import com.mustfaibra.roffu.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FicheExchangeViewModel @Inject constructor(
    private val exchangeService: ExchangeService,
    public val sessionService: SessionService
) : ViewModel() {

    private val _currentExchanges = MutableStateFlow<List<Exchange>>(emptyList())
    val currentExchanges: StateFlow<List<Exchange>> get() = _currentExchanges

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _exchange = MutableStateFlow<Exchange?>(null)
    val exchange: StateFlow<Exchange?> get() = _exchange

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

    fun fetchExchangeById(exchangeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = exchangeService.getExchangeById(exchangeId.toString())
            if (response.isSuccessful) {
                _exchange.value = response.body() as Exchange
            } else {
                // Handle error case
            }
            _isLoading.value = false
        }
    }

    fun getExchangeById(exchangeId: Int): StateFlow<Exchange?> {
        fetchExchangeById(exchangeId)
        return _exchange
    }
}
