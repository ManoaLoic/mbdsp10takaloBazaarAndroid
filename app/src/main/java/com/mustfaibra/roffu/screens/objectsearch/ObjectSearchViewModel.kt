package com.mustfaibra.roffu.screens.objectsearch

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.models.Category
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.screens.category.CategoryViewModel
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

    var name: String = ""
    var description: String = ""
    var categoryId: Int? = null
    var createdAtStart: String = ""
    var createdAtEnd: String = ""
    var sortBy: String = "asc"
    var pageNo: Int = 1
    var pageSize: Int = 12

//    init {
//        loadObjects()
//    }

    fun loadObjects() {
        _isLoading.value = true
        viewModelScope.launch {
            val response = objectService.getObjects(
                pageNo,
                pageSize,
                sortBy,
                name.ifBlank { null },
                description.ifBlank { null },
                categoryId,
                createdAtStart.ifBlank { null },
                createdAtEnd.ifBlank { null }
            )
            if (response.isSuccessful) {
                _objects.value = response.body()?.data?.objects ?: emptyList()
            } else {
                _objects.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun resetFilters() {
        name = ""
        description = ""
        categoryId = null
        createdAtStart = ""
        createdAtEnd = ""
        loadObjects()
    }
}
