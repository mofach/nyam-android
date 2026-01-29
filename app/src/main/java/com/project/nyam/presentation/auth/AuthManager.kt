package com.project.nyam.presentation.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.project.nyam.data.SessionManager
import com.project.nyam.data.model.LoginRequest
import com.project.nyam.data.remote.ApiClient
import com.project.nyam.data.model.UserData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val sessionManager = SessionManager(context)

    // Scope untuk menjalankan coroutine di fungsi non-suspend
    private val scope = MainScope()

    suspend fun getIdToken(): String? {
        return try {
            auth.currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("956299547913-t8cicol323thaaugdpchc46ni4nb88he.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // REVISI: Menggunakan scope.launch untuk memanggil clearSession()
    fun signOutGoogle(onComplete: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            auth.signOut()
            scope.launch {
                sessionManager.clearSession()
                onComplete()
            }
        }
    }

    suspend fun handleGoogleSignIn(googleIdToken: String): UserData? {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseIdToken = authResult.user?.getIdToken(true)?.await()?.token

            if (firebaseIdToken != null) {
                val response = ApiClient.instance.loginWithGoogle(LoginRequest(firebaseIdToken))
                if (response.isSuccessful) {
                    val userData = response.body()?.data
                    userData?.let {
                        sessionManager.saveSession(
                            uid = it.uid,
                            name = it.name,
                            isDone = it.isOnboardingCompleted,
                            needs = it.nutritionalNeeds
                        )
                    }
                    userData
                } else null
            } else null
        } catch (e: Exception) { null }
    }

    suspend fun loginWithEmail(email: String, pass: String): UserData? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val token = result.user?.getIdToken(true)?.await()?.token
            if (token != null) {
                val response = ApiClient.instance.loginWithGoogle(LoginRequest(token))
                if (response.isSuccessful) {
                    val userData = response.body()?.data
                    userData?.let {
                        sessionManager.saveSession(
                            uid = it.uid,
                            name = it.name,
                            isDone = it.isOnboardingCompleted,
                            needs = it.nutritionalNeeds
                        )
                    }
                    userData
                } else null
            } else null
        } catch (e: Exception) { null }
    }
}