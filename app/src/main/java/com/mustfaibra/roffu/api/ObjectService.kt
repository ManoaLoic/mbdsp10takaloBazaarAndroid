package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.models.ObjectListResponse
import com.mustfaibra.roffu.models.ObjectRequest
import com.mustfaibra.roffu.models.UpdateObject
import com.mustfaibra.roffu.models.UpdateObjectResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ObjectService {
    @PATCH("object/{objectId}/repost")
    suspend fun repostObject(
        @Path("objectId") objectId: Int
    ): Response<Object>

    @GET("object/{id}")
    suspend fun getObjectById(@Path("id") id: Int): Response<Object>

    @PUT("objects/{id}")
    suspend fun updateObject(@Path("id") id: Int, @Body objectRequest: UpdateObject): Response<UpdateObjectResponse>


    @GET("user/{userId}/objects")
    suspend fun getUserObjects(
        @Path("userId") userId: Int,
        @QueryMap params: Map<String, Int>
    ): Response<ObjectListResponse>

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

    @PATCH("object/{objectId}/remove")
    suspend fun removeObject(@Path("objectId") objectId: Int): Response<Any>
}
