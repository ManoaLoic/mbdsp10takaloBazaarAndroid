package com.mustfaibra.roffu.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val user: LoginUser
)

data class LoginUser(
    val token: String,
    val username: String,
    val id: Int
)

@Serializable
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String
)