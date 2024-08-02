package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    suspend fun getCategories(): Response<CategoryResponse>
}
