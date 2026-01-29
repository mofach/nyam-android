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
    var tempUid by remember { mutableStateOf(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "") }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") { OnboardingScreen { navController.navigate("login") { popUpTo("onboarding") { inclusive = true } } } }

        composable("login") {
            LoginScreen(onAuthSuccess = { userData ->
                tempUid = userData.uid
                scope.launch {
                    sessionManager.saveSession(userData.uid, userData.name, userData.isOnboardingCompleted, userData.nutritionalNeeds)
                    navController.navigate(if (userData.isOnboardingCompleted) "home" else "physical_data")
                }
            }, onNavigateToRegister = { navController.navigate("register") })
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = { userData ->
                if (userData != null) {
                    tempUid = userData.uid
                    scope.launch {
                        sessionManager.saveSession(userData.uid, userData.name, userData.isOnboardingCompleted, userData.nutritionalNeeds)
                        navController.navigate(if (userData.isOnboardingCompleted) "home" else "physical_data")
                    }
                } else { navController.navigate("login") }
            }, onBack = { navController.popBackStack() })
        }

        composable("physical_data") {
            PhysicalDataScreen(uid = tempUid) {
                scope.launch { sessionManager.saveSession(tempUid, isDone = true); navController.navigate("home") }
            }
        }

        composable("home") {
            var fullProfile by remember { mutableStateOf<FullUserProfile?>(null) }
            var historyData by remember { mutableStateOf<HistoryData?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            // Fungsi muat data yang bisa dipanggil berulang
            val loadData: ( ((Boolean) -> Unit)? ) -> Unit = { onFinished ->
                scope.launch {
                    try {
                        val token = authManager.getIdToken()
                        if (token != null && tempUid.isNotEmpty()) {
                            val pRes = ApiClient.instance.getProfile("Bearer $token", tempUid)
                            val hRes = ApiClient.instance.getTodayHistory("Bearer $token")
                            if (pRes.isSuccessful) fullProfile = pRes.body()?.data
                            if (hRes.isSuccessful) historyData = hRes.body()?.data
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Koneksi bermasalah", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                        // Callback untuk memberitahu UI bahwa refresh selesai
                        onFinished?.invoke(false)
                    }
                }
            }

            LaunchedEffect(Unit) { loadData(null) }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                fullProfile?.let { profile ->
                    DashboardScreen(
                        userData = profile,
                        historyData = historyData,
                        onNavigateToProfile = { /* Nav ke Profile */ },
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
                                if (ApiClient.instance.updateProfile("Bearer $token", tempUid, req).isSuccessful) {
                                    loadData(null) // Refresh setelah update TDEE
                                }
                            }
                        },
                        onRefresh = { onFinished -> loadData(onFinished) } // Trigger refresh mulus
                    )
                }
            }
        }
    }
}