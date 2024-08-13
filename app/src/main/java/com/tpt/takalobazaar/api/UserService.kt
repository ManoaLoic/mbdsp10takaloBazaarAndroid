package com.tpt.takalobazaar.api

import com.tpt.takalobazaar.models.CustomUser
import com.tpt.takalobazaar.models.UpdateUserRequest
import com.tpt.takalobazaar.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @PUT("user/{id}")
    suspend fun updateUserProfile(@Path("id") id: Int, @Body data: UpdateUserRequest): Response<UserResponse>

    @GET("user/{id}")
    suspend fun getUserProfile(@Path("id") id: Int): Response<UserResponse>
}
