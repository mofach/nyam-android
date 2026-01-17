package com.project.nyam.data.remote

import com.project.nyam.data.model.AuthResponse
import com.project.nyam.data.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.project.nyam.data.model.PhysicalDataRequest
import com.project.nyam.data.model.PhysicalDataResponse
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: LoginRequest): Response<AuthResponse>

    // Endpoint Update Data Fisik
    @PUT("api/users/{uid}/profile")
    suspend fun updateProfile(
        @Path("uid") uid: String,
        @Body request: PhysicalDataRequest
    ): Response<PhysicalDataResponse>
}