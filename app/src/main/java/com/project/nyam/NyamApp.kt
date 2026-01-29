package com.project.nyam

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.project.nyam.data.SessionManager
import com.project.nyam.data.model.*
import com.project.nyam.data.remote.ApiClient
import com.project.nyam.presentation.auth.*
import com.project.nyam.presentation.dashboard.DashboardScreen
import com.project.nyam.presentation.onboarding.*
import kotlinx.coroutines.launch

@Composable
fun NyamApp(startDestination: String = "onboarding") {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val authManager = remember { AuthManager(context) }

    // Inisialisasi UID secara reaktif dari Firebase
    var tempUid by remember {
        mutableStateOf(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "")
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // --- ONBOARDING & AUTH ---
        composable("onboarding") {
            OnboardingScreen {
                navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
            }
        }

        composable("login") {
            LoginScreen(onAuthSuccess = { userData ->
                tempUid = userData.uid
                scope.launch {
                    sessionManager.saveSession(userData.uid, userData.name, userData.isOnboardingCompleted, userData.nutritionalNeeds)
                    navController.navigate(if (userData.isOnboardingCompleted) "home" else "physical_data") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }, onNavigateToRegister = { navController.navigate("register") })
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = { userData ->
                if (userData != null) {
                    tempUid = userData.uid
                    scope.launch {
                        sessionManager.saveSession(userData.uid, userData.name, userData.isOnboardingCompleted, userData.nutritionalNeeds)
                        navController.navigate(if (userData.isOnboardingCompleted) "home" else "physical_data") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate("login")
                }
            }, onBack = { navController.popBackStack() })
        }

        composable("physical_data") {
            PhysicalDataScreen(uid = tempUid) {
                scope.launch {
                    sessionManager.saveSession(tempUid, isDone = true)
                    navController.navigate("home") {
                        popUpTo("physical_data") { inclusive = true }
                    }
                }
            }
        }

        // --- DASHBOARD (HOME) ---
        composable("home") {
            var fullProfile by remember { mutableStateOf<FullUserProfile?>(null) }
            var historyData by remember { mutableStateOf<HistoryData?>(null) }
            var recommendations by remember { mutableStateOf<List<Recipe>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }

            // Fungsi muat data terpusat
            val loadData: ( ((Boolean) -> Unit)? ) -> Unit = { onFinished ->
                scope.launch {
                    try {
                        val token = authManager.getIdToken()
                        if (token != null && tempUid.isNotEmpty()) {
                            val pRes = ApiClient.instance.getProfile("Bearer $token", tempUid)
                            val hRes = ApiClient.instance.getTodayHistory("Bearer $token")
                            val rRes = ApiClient.instance.getSmartRecommendations("Bearer $token")

                            if (pRes.isSuccessful) fullProfile = pRes.body()?.data
                            if (hRes.isSuccessful) historyData = hRes.body()?.data
                            if (rRes.isSuccessful) {
                                recommendations = rRes.body()?.data?.recipes ?: emptyList()
                            }

                            if (!pRes.isSuccessful) {
                                Toast.makeText(context, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Koneksi bermasalah", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                        onFinished?.invoke(false)
                    }
                }
            }

            LaunchedEffect(tempUid) {
                if (tempUid.isNotEmpty()) loadData(null)
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                fullProfile?.let { profile ->
                    DashboardScreen(
                        userData = profile,
                        historyData = historyData,
                        recommendations = recommendations,
                        onNavigateToProfile = { /* Nav ke Profile Screen nanti */ },
                        // --- LOGIKA BARU: MASAK & MAKAN ---
                        onCookMeal = { mealReq ->
                            scope.launch {
                                try {
                                    val token = authManager.getIdToken()
                                    val response = ApiClient.instance.logMeal("Bearer $token", mealReq)
                                    if (response.isSuccessful) {
                                        loadData(null) // Refresh gizi & progres bar
                                        Toast.makeText(context, "Nyam! Makanan berhasil dicatat", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Gagal mencatat makanan", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onUpdateTdee = { newVal ->
                            scope.launch {
                                val token = authManager.getIdToken()
                                val req = PhysicalDataRequest(
                                    name = profile.name,
                                    birthdate = profile.birthdate ?: "2000-01-01",
                                    gender = profile.physicalData.gender,
                                    height = profile.physicalData.height,
                                    weight = profile.physicalData.weight,
                                    activityLevel = newVal,
                                    allergies = profile.preferences.allergies
                                )
                                val updateRes = ApiClient.instance.updateProfile("Bearer $token", tempUid, req)
                                if (updateRes.isSuccessful) loadData(null)
                            }
                        },
                        onRefresh = { onFinished -> loadData(onFinished) }
                    )
                } ?: run {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Data tidak ditemukan, tarik untuk refresh")
                    }
                }
            }
        }
    }
}