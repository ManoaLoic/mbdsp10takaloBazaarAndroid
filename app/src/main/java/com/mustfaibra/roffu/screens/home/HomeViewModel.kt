package com.mustfaibra.roffu.screens.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.models.Advertisement
import com.mustfaibra.roffu.sealed.DataResponse
import com.mustfaibra.roffu.sealed.Error
import com.mustfaibra.roffu.sealed.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val objectService: ObjectService,
) : ViewModel() {

    private val _objects = MutableStateFlow<List<com.mustfaibra.roffu.models.Object>>(emptyList())
    val objects: StateFlow<List<com.mustfaibra.roffu.models.Object>> get() = _objects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    var pageNo: Int = 1
    var pageSize: Int = 20

    val searchQuery = mutableStateOf("")

    val homeAdvertisementsUiState = mutableStateOf<UiState>(UiState.Success)
    val advertisements: MutableList<Advertisement> = mutableStateListOf()

    val brandsUiState = mutableStateOf<UiState>(UiState.Loading)

    val currentSelectedBrandIndex = mutableStateOf(0)

    fun updateCurrentSelectedBrandId(index: Int) {
        currentSelectedBrandIndex.value = index
    }

    fun updateSearchInputValue(value: String) {
        this.searchQuery.value = value
    }

    fun loadObjects() {
        if (_isLoading.value) return

        _isLoading.value = true
        viewModelScope.launch {
            val response = objectService.getObjects(
                pageNo,
                pageSize,
                "DESC",
                null,
                null,
                null,
                null,
                null
            )
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    _objects.value = data.objects
                }
            }
            _isLoading.value = false
        }
    }
}
