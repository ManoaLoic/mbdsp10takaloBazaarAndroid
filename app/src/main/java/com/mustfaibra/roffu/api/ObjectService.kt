package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.Object
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("objects/{id}")
    suspend fun getObjectById(@Path("id") id: Int): Response<Object>

    @POST("objects")
    suspend fun createObject(@Body obj: Object): Response<Object>
}
