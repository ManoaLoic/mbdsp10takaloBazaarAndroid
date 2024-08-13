package com.tpt.takalobazaar.api

import com.tpt.takalobazaar.models.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    suspend fun getCategories(): Response<CategoryResponse>
}
