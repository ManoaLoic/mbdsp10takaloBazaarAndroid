package com.tpt.takalobazaar.screens.objectsearch

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.models.Object
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectSearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val objectService: ObjectService
) : ViewModel() {

    private val _objects = MutableStateFlow<List<Object>>(emptyList())
    val objects: StateFlow<List<Object>> get() = _objects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> get() = _hasMorePages

    var name = mutableStateOf("")
    var description = mutableStateOf("")
    var categoryId: Int? = null
    var createdAtStart = mutableStateOf("")
    var createdAtEnd = mutableStateOf("")
    var sortBy: String = "DESC"
    var pageNo: Int = 1
    var pageSize: Int = 20

    fun loadObjects(resetPage: Boolean = false) {
        if (_isLoading.value) return

        if (resetPage) {
            pageNo = 1
            _objects.value = emptyList()
        }

        _isLoading.value = true
        viewModelScope.launch {
            val response = objectService.getObjects(
                pageNo,
                pageSize,
                sortBy,
                name.value.ifBlank { null },
                description.value.ifBlank { null },
                categoryId,
                createdAtStart.value.ifBlank { null },
                createdAtEnd.value.ifBlank { null }
            )
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    _objects.value = _objects.value + data.objects
                    _hasMorePages.value = data.currentPage < data.totalPages
                }
            } else {
                _hasMorePages.value = false
            }
            _isLoading.value = false
        }
    }

    fun resetFilters() {
        name.value = ""
        description.value = ""
        categoryId = null
        createdAtStart.value = ""
        createdAtEnd.value = ""
        loadObjects(resetPage = true)
    }

    fun loadNextPage() {
        if (_hasMorePages.value) {
            pageNo++
            loadObjects()
        }
    }
}