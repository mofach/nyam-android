package com.project.nyam.data.remote

import com.project.nyam.data.model.AuthResponse
import com.project.nyam.data.model.HistoryResponse
import com.project.nyam.data.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.project.nyam.data.model.PhysicalDataRequest
import com.project.nyam.data.model.PhysicalDataResponse
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.GET

interface ApiService {
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: LoginRequest): Response<AuthResponse>

    // Endpoint Update Data Fisik
    @PUT("api/users/{uid}/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Body request: PhysicalDataRequest
    ): Response<PhysicalDataResponse>

    @GET("api/tracker/history")
    suspend fun getTodayHistory(
        @Header("Authorization") token: String
    ): Response<HistoryResponse>

    @GET("api/users/{uid}/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("uid") uid: String
    ): Response<PhysicalDataResponse>
}