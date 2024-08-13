package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.CustomUser
import com.mustfaibra.roffu.models.UpdateUserRequest
import com.mustfaibra.roffu.models.UserResponse
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
