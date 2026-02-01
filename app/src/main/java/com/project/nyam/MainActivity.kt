package com.project.nyam

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // IMPORT INI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Panggil installSplashScreen sebelum super.onCreate dan setContent
        // Ini akan menampilkan logo NYAM selama aplikasi menyiapkan resource
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // LOGIKA ASLI KAMU TETAP UTUH DI SINI
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Cek apakah user pernah melewati onboarding sebelumnya
        val sharedPref = getSharedPreferences("nyam_prefs", Context.MODE_PRIVATE)
        val hasSeenOnboarding = sharedPref.getBoolean("has_seen_onboarding", false)

        setContent {
            // Alur navigasi kamu tetap tidak berubah
            val startScreen = when {
                currentUser != null -> "home"
                !hasSeenOnboarding -> "onboarding"
                else -> "login"
            }

            NyamApp(startDestination = startScreen)
        }
    }
}