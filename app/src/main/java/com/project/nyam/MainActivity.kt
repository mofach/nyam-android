package com.project.nyam

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        // Cek apakah user pernah melewati onboarding sebelumnya (disimpan secara permanen di SP)
        val sharedPref = getSharedPreferences("nyam_prefs", Context.MODE_PRIVATE)
        val hasSeenOnboarding = sharedPref.getBoolean("has_seen_onboarding", false)

        setContent {
            val startScreen = when {
                currentUser != null -> "home"
                !hasSeenOnboarding -> "onboarding"
                else -> "login" // Jika sudah pernah onboarding tapi belum login
            }

            NyamApp(startDestination = startScreen)
        }
    }
}