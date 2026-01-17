package com.project.nyam.presentation.auth

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider // <--- SUDAH DITAMBAHKAN
import com.project.nyam.data.model.LoginRequest
import com.project.nyam.data.remote.ApiClient
import kotlinx.coroutines.tasks.await // <--- BUTUH LIBRARY DI LANGKAH 1
import com.project.nyam.data.model.UserData

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

    // Ganti xxxxx dengan Web Client ID dari google-services.json kamu (Client Type 3)
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("956299547913-t8cicol323thaaugdpchc46ni4nb88he.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    fun signOutGoogle(onComplete: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            auth.signOut()
            onComplete()
        }
    }

    suspend fun handleGoogleSignIn(googleIdToken: String): UserData? {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseIdToken = authResult.user?.getIdToken(true)?.await()?.token

            if (firebaseIdToken != null) {
                val response = ApiClient.instance.loginWithGoogle(LoginRequest(firebaseIdToken))
                if (response.isSuccessful) response.body()?.data else null
            } else null
        } catch (e: Exception) { null }
    }

    suspend fun loginWithEmail(email: String, pass: String): UserData? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val token = result.user?.getIdToken(true)?.await()?.token
            if (token != null) {
                val response = ApiClient.instance.loginWithGoogle(LoginRequest(token))
                if (response.isSuccessful) response.body()?.data else null
            } else null
        } catch (e: Exception) { null }
    }
}