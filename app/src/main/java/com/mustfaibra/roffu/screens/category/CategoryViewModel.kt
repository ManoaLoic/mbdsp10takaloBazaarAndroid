package com.mustfaibra.roffu.screens.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.api.CategoryService
import com.mustfaibra.roffu.api.RetrofitInstance
import com.mustfaibra.roffu.models.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class CategoryViewModel : ViewModel() {

    private val categoryService: CategoryService = RetrofitInstance.createService(CategoryService::class.java)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            retryFetchCategories()
        }
    }

    private suspend fun retryFetchCategories(retries: Int = 3, initialDelay: Long = 1000, maxDelay: Long = 3000, factor: Double = 2.0) {
        var currentDelay = initialDelay
        var success = false

        repeat(retries) { attempt ->
            try {
                _isLoading.value = true
                withTimeout(5000) { // Timeout after 5 seconds
                    val response = categoryService.getCategories()
                    if (response.isSuccessful) {
                        val fetchedCategories = response.body()?.data?.categories ?: emptyList()
                        _categories.value = fetchedCategories
                        Log.d("CategoryViewModel", "Fetched categories: $fetchedCategories")
                        success = true
                        return@withTimeout
                    } else {
                        Log.e("CategoryViewModel", "Error fetching categories: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("CategoryViewModel", "Timeout error fetching categories: $e")
            } catch (e: IOException) {
                Log.e("CategoryViewModel", "Network error fetching categories: $e")
            } catch (e: HttpException) {
                Log.e("CategoryViewModel", "HTTP error fetching categories: $e")
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Unexpected error fetching categories: $e")
            } finally {
                _isLoading.value = false
            }

            if (success) return@repeat
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }

        if (!success) {
            Log.e("CategoryViewModel", "Failed to fetch categories after $retries attempts")
        }
    }
}
