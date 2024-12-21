package com.dicoding.dicodingstory.api

import com.dicoding.dicodingstory.model.Story
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Query

data class RegisterRequest(val name: String, val email: String, val password: String)
data class RegisterResponse(val error: Boolean, val message: String)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val error: Boolean, val message: String, val loginResult: LoginResult)
data class LoginResult(val userId: String, val name: String, val token: String)

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("stories")
    fun getStories(@Header("Authorization") token: String): Call<StoriesResponse>

    @GET("stories/{id}")
    fun getStoryDetails(@Header("Authorization") token: String, @Path("id") storyId: String): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<StoryResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Call<StoriesResponse>
} 