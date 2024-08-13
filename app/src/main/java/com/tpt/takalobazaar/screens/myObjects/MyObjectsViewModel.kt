package com.tpt.takalobazaar.screens.myobjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyObjectsViewModel @Inject constructor(
    private val objectService: ObjectService,
    private val sessionService: SessionService
) : ViewModel() {

    private val _userObjects = MutableStateFlow<List<Object>>(emptyList())
    val userObjects: StateFlow<List<Object>> get() = _userObjects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> get() = _hasMorePages

    var pageNo: Int = 1
    var pageSize: Int = 20

    fun loadUserObjects(resetPage: Boolean = false) {
        if (_isLoading.value) return

        if (resetPage) {
            pageNo = 1
            _userObjects.value = emptyList()
        }

        _isLoading.value = true
        viewModelScope.launch {
            val user = sessionService.getUser()
            user?.let {
                val params = mapOf(
                    "page" to pageNo,
                    "limit" to pageSize
                )
                val response = objectService.getUserObjects(it.id, params)
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        _userObjects.value = _userObjects.value + data.objects
                        _hasMorePages.value = data.currentPage < data.totalPages
                    }
                } else {
                    _hasMorePages.value = false
                }
            }
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        if (_hasMorePages.value) {
            pageNo++
            loadUserObjects()
        }
    }
}


