package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.LoginRequest
import com.mustfaibra.roffu.models.LoginResponse
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.models.ObjectRequest
import com.mustfaibra.roffu.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthentificationService {

    @POST("auth/user/login")
    suspend fun login(@Body obj: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body obj: RegisterRequest): Response<LoginUser>

    @POST("auth/logout")
    suspend fun logout(): Response<String>

}