package com.project.nyam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek status login di Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser

        setContent {
            // Jika user sudah login, startDestination langsung ke "home"
            // Jika belum, mulai dari "onboarding"
            val startScreen = if (currentUser != null) "home" else "onboarding"

            NyamApp(startDestination = startScreen)
        }
    }
}