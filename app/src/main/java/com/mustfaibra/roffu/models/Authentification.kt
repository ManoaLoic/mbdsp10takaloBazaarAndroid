package com.mustfaibra.roffu.models

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