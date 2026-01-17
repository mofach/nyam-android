package com.project.nyam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.nyam.data.SessionManager
import com.project.nyam.presentation.auth.LoginScreen
import com.project.nyam.presentation.auth.RegisterScreen
import com.project.nyam.presentation.onboarding.OnboardingScreen
import com.project.nyam.presentation.onboarding.PhysicalDataScreen
import kotlinx.coroutines.launch

@Composable
fun NyamApp(startDestination: String = "onboarding") {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    // Ambil UID jika user sudah login sebelumnya
    var tempUid by remember {
        mutableStateOf(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "")
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // --- ONBOARDING ---
        composable("onboarding") {
            OnboardingScreen(onNavigateToLogin = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        // --- LOGIN ---
        composable("login") {
            LoginScreen(
                onAuthSuccess = { userData ->
                    tempUid = userData.uid
                    scope.launch {
                        sessionManager.saveSession(userData.uid, userData.isOnboardingCompleted)

                        // Navigasi berdasarkan status onboarding dari backend
                        if (userData.isOnboardingCompleted) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("physical_data") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // --- REGISTER ---
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { userData ->
                    if (userData != null) {
                        // Jika daftar via Google dan sukses login backend
                        if (userData.isOnboardingCompleted) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("physical_data") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    } else {
                        // Jika daftar email manual sukses, balik ke login
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- INPUT DATA FISIK ---
        composable("physical_data") {
            PhysicalDataScreen(uid = tempUid, onComplete = {
                navController.navigate("home") {
                    // Bersihkan stack agar tidak bisa kembali ke form data fisik
                    popUpTo("physical_data") { inclusive = true }
                }
            })
        }

        // --- HOME / DASHBOARD ---
        composable("home") {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Dashboard Home - Kamu Sudah Login!")
            }
        }
    }
}