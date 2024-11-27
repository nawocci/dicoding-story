package com.dicoding.dicodingstory.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val name: String, val email: String, val password: String)
data class RegisterResponse(val error: Boolean, val message: String)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val error: Boolean, val message: String, val loginResult: LoginResult)
data class LoginResult(val userId: String, val name: String, val token: String)

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
} 