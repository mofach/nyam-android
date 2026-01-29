package com.project.nyam.data.model

import com.google.gson.annotations.SerializedName
import androidx.compose.ui.graphics.vector.ImageVector

// --- REQUEST MODELS ---
data class LoginRequest(
    @SerializedName("idToken") val idToken: String
)

data class PhysicalDataRequest(
    val name: String,
    val birthdate: String,
    val gender: Int,
    val height: Int,
    val weight: Int,
    val activityLevel: Double,
    val allergies: List<String>
)

// --- RESPONSE MODELS ---
data class AuthResponse(
    val status: String,
    val message: String,
    val data: UserData?
)

data class UserData(
    val uid: String,
    val email: String,
    val name: String?,
    val photoUrl: String?,
    val isOnboardingCompleted: Boolean,
    val healthStats: HealthStats? = null,
    val nutritionalNeeds: NutritionalNeeds? = null
)

data class PhysicalDataResponse(
    val status: String,
    val message: String,
    @SerializedName("data")
    val data: FullUserProfile?
)

data class FullUserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val birthdate: String?,
    val isOnboardingCompleted: Boolean,
    val healthStats: HealthStats,
    val nutritionalNeeds: NutritionalNeeds,
    val physicalData: PhysicalDetail,
    val preferences: PreferencesDetail
)

data class HealthStats(
    val bmi: Double,
    val bmiStatus: String,
    val bmr: Int,
    val tdee: Int,
    val bmrScore: Int,
    val bmrLabel: String
)

data class PreferencesDetail(
    val allergies: List<String>
)

data class NutritionalNeeds(
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

data class PhysicalDetail(
    val gender: Int,
    val age: Int,
    val height: Int,
    val weight: Int,
    val activityLevel: Double
)

// --- HISTORY MODELS ---
data class HistoryResponse(
    val status: String,
    val data: HistoryData?
)

data class HistoryData(
    val summary: NutritionSummary?,
    val target: NutritionalNeeds?,
    val meals: List<Meal>?
)

data class NutritionSummary(
    val totalCalories: Int,
    val totalCarbs: Int,
    val totalProtein: Int,
    val totalFat: Int
)

data class Meal(
    val foodName: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val imageUrl: String?
)

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)