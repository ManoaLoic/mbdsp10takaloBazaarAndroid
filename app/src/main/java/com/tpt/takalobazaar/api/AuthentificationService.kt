package com.tpt.takalobazaar.api

import com.tpt.takalobazaar.models.LoginRequest
import com.tpt.takalobazaar.models.LoginResponse
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.models.ObjectRequest
import com.tpt.takalobazaar.models.RegisterRequest
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