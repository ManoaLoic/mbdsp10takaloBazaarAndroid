package com.tpt.takalobazaar.screens.ExchangeHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.ExchangeService
import com.tpt.takalobazaar.models.Exchange
import com.tpt.takalobazaar.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeHistoryViewModel @Inject constructor(
    private val exchangeService: ExchangeService,
    private val sessionService: SessionService
) : ViewModel() {

    private val _exchanges = MutableStateFlow<List<Exchange>>(emptyList())
    val exchanges: StateFlow<List<Exchange>> get() = _exchanges

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private var currentPage = 1
    private var totalPages = 1

    private val _status = MutableStateFlow("Tous")
    val status: StateFlow<String> get() = _status

    fun loadExchangeHistory() {
        if (currentPage > totalPages) {
            return  // Stop loading if there are no more pages
        }

        viewModelScope.launch {
            _isLoading.value = true
            val userId = sessionService.getUser()?.id.toString()
            val apiStatus = Exchange.convertStatusToApiFormat(_status.value)
            val response = exchangeService.getExchangeHistory(userId = userId, page = currentPage, status = apiStatus)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    _exchanges.value = _exchanges.value + data.exchanges
                    currentPage++
                    totalPages = data.totalPages
                }
            }
            _isLoading.value = false
        }
    }

    fun loadMoreExchanges() {
        loadExchangeHistory()
    }

    fun updateStatus(newStatus: String) {
        _status.value = newStatus
        currentPage = 1
        totalPages = 1
        _exchanges.value = emptyList()
        loadExchangeHistory()
    }
}
