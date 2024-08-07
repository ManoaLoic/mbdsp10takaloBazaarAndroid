package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.models.ObjectListResponse
import com.mustfaibra.roffu.models.ObjectRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ObjectService {
    @GET("object/{id}")
    suspend fun getObjectById(@Path("id") id: Int): Response<Object>

    @POST("objects")
    suspend fun createObject(@Body obj: ObjectRequest): Response<Object>

    @GET("objects")
    suspend fun getObjects(
        @Query("page") pageNo: Int,
        @Query("limit") pageSize: Int,
        @Query("order_direction") sortBy: String,
        @Query("name") name: String?,
        @Query("description") description: String?,
        @Query("category_id") categoryId: Int?,
        @Query("created_at_start") createdAtStart: String?,
        @Query("created_at_end") createdAtEnd: String?
    ): Response<ObjectListResponse>
}
