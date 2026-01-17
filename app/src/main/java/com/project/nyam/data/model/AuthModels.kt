package com.project.nyam.data.model

import com.google.gson.annotations.SerializedName

// 1. Yang kita KIRIM ke Backend (Request Body)
data class LoginRequest(
    @SerializedName("idToken")
    val idToken: String
)

// 2. Yang kita TERIMA dari Backend (Response)
data class AuthResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData? // Bisa null kalau error
)

// 3. Detail Data User
data class UserData(
    @SerializedName("uid")
    val uid: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("photoUrl")
    val photoUrl: String?,

    @SerializedName("isOnboardingCompleted")
    val isOnboardingCompleted: Boolean // <--- INI KUNCI NAVIGASI KITA
)

// Request untuk PUT /api/users/{uid}/profile
data class PhysicalDataRequest(
    val name: String,
    val birthdate: String, // YYYY-MM-DD
    val gender: Int,       // 0: Male, 1: Female
    val height: Int,
    val weight: Int,
    val activityLevel: Double,
    val allergies: List<String>
)

// Response setelah simpan data fisik
data class PhysicalDataResponse(
    val status: String,
    val message: String,
    val data: PhysicalDataDetail?
)

data class PhysicalDataDetail(
    val healthStats: HealthStats
)

data class HealthStats(
    val bmi: Double,
    val bmiStatus: String,
    val bmr: Int,
    val tdee: Int
)