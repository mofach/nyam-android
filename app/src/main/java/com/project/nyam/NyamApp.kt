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
import com.project.nyam.presentation.profile.ProfileScreen
import com.project.nyam.presentation.profile.EditProfileScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NyamApp(startDestination: String = "onboarding") {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val authManager = remember { AuthManager(context) }

    // --- GLOBAL STATES ---
    var fullProfile by remember { mutableStateOf<FullUserProfile?>(null) }
    var historyData by remember { mutableStateOf<HistoryData?>(null) }
    var recommendations by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }
    var tempUid by remember {
        mutableStateOf(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "")
    }

    // --- FUNGSI LOAD DATA ---
    val loadData: (((Boolean) -> Unit)?) -> Unit = { onFinished ->
        scope.launch {
            try {
                val token = authManager.getIdToken()
                if (token != null && tempUid.isNotEmpty()) {
                    val pRes = ApiClient.instance.getProfile("Bearer $token", tempUid)

                    if (pRes.isSuccessful) {
                        fullProfile = pRes.body()?.data
                        val hRes = ApiClient.instance.getTodayHistory("Bearer $token")
                        val rRes = ApiClient.instance.getSmartRecommendations("Bearer $token")
                        if (hRes.isSuccessful) historyData = hRes.body()?.data
                        if (rRes.isSuccessful) recommendations =
                            rRes.body()?.data?.recipes ?: emptyList()
                    } else if (pRes.code() == 404 || pRes.code() == 401) {
                        // Jika akun dihapus, navigasi dulu baru clear
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        sessionManager.clearSession()
                        fullProfile = null
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Masalah koneksi", Toast.LENGTH_SHORT).show()
            } finally {
                isLoadingData = false
                onFinished?.invoke(false)
            }
        }
    }
        NavHost(navController = navController, startDestination = startDestination) {

            composable("onboarding") {
                OnboardingScreen {
                    // Simpan status bahwa user sudah melihat onboarding selamanya
                    val sharedPref = context.getSharedPreferences("nyam_prefs", android.content.Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("has_seen_onboarding", true).apply()

                    navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
                }
            }

            composable("login") {
                LoginScreen(onAuthSuccess = { userData ->
                    tempUid = userData.uid
                    scope.launch {
                        sessionManager.saveSession(
                            userData.uid,
                            userData.name,
                            userData.isOnboardingCompleted,
                            userData.nutritionalNeeds
                        )
                        val route = if (userData.isOnboardingCompleted) "home" else "physical_data"
                        navController.navigate(route) { popUpTo("login") { inclusive = true } }
                    }
                }, onNavigateToRegister = { navController.navigate("register") })
            }

            composable("register") {
                RegisterScreen(onRegisterSuccess = { userData ->
                    if (userData != null) {
                        tempUid = userData.uid
                        scope.launch {
                            sessionManager.saveSession(
                                userData.uid,
                                userData.name,
                                userData.isOnboardingCompleted
                            )
                            navController.navigate("physical_data") {
                                popUpTo("register") {
                                    inclusive = true
                                }
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
                            popUpTo("physical_data") {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            composable("home") {
                LaunchedEffect(Unit) {
                    val token = authManager.getIdToken()
                    if (token == null) {
                        navController.navigate("login") { popUpTo(0) }
                        return@LaunchedEffect
                    }
                    val isDone = sessionManager.isOnboardingDone.first()
                    if (!isDone) {
                        navController.navigate("physical_data") {
                            popUpTo("home") {
                                inclusive = true
                            }
                        }
                    } else {
                        if (tempUid.isNotEmpty()) loadData(null)
                    }
                }

                if (isLoadingData) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                } else {
                    fullProfile?.let { profile ->
                        DashboardScreen(
                            userData = profile,
                            historyData = historyData,
                            recommendations = recommendations,
                            onNavigateToProfile = { navController.navigate("profile") },
                            onCookMeal = { mealReq ->
                                scope.launch {
                                    val token = authManager.getIdToken()
                                    if (ApiClient.instance.logMeal(
                                            "Bearer $token",
                                            mealReq
                                        ).isSuccessful
                                    ) loadData(null)
                                }
                            },
                            onUpdateTdee = { newVal ->
                                scope.launch {
                                    val token = authManager.getIdToken()
                                    val req = PhysicalDataRequest(
                                        profile.name,
                                        profile.birthdate ?: "2000-01-01",
                                        profile.physicalData.gender,
                                        profile.physicalData.height,
                                        profile.physicalData.weight,
                                        newVal,
                                        profile.preferences.allergies
                                    )
                                    if (ApiClient.instance.updateProfile(
                                            "Bearer $token",
                                            tempUid,
                                            req
                                        ).isSuccessful
                                    ) loadData(null)
                                }
                            },
                            onRefresh = { onFinished -> loadData(onFinished) }
                        )
                    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Menyiapkan dashboard...")
                    }
                }
            }

            composable("profile") {
                ProfileScreen(
                    userData = fullProfile,
                    onBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate("edit_profile") },
                    onLogout = {
                        // PINDAH LAYAR DULU BIAR GA CRASH
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }

                        scope.launch {
                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            fullProfile = null
                            historyData = null
                            recommendations = emptyList()
                            tempUid = ""
                            isLoadingData = true
                        }
                    }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    userData = fullProfile,
                    onBack = { navController.popBackStack() },
                    onSave = { n, b, g, h, w, a, al ->
                        scope.launch {
                            try {
                                val token = authManager.getIdToken()
                                val req = PhysicalDataRequest(n, b, g, h, w, a, al)
                                if (ApiClient.instance.updateProfile(
                                        "Bearer $token",
                                        tempUid,
                                        req
                                    ).isSuccessful
                                ) {
                                    loadData { navController.popBackStack() }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                )
            }
        }
    }
