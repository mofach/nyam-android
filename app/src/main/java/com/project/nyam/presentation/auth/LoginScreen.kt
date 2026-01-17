package com.project.nyam.presentation.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.ExperimentalComposeUiApi // Untuk anotasi OptIn

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onAuthSuccess: (com.project.nyam.data.model.UserData) -> Unit,
    onNavigateToRegister: () -> Unit // Parameter untuk pindah ke halaman register
) {
    // --- KONTEKS & LOGIKA ---
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { AuthManager(context) }

    // --- STATE UI ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val HijauNyam = Color(0xFF4CAF50)
    val PutihBersih = Color(0xFFFFFFFF)

    // --- LAUNCHER GOOGLE SIGN IN ---
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                scope.launch {
                    val userData = authManager.handleGoogleSignIn(idToken)
                    if (userData != null) {
                        Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        onAuthSuccess(userData)
                    } else {
                        Toast.makeText(context, "Gagal verifikasi ke Server NYAM", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Login Google Dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // --- TAMPILAN UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PutihBersih)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome Back!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = HijauNyam)
        Text(text = "Masuk untuk melanjutkan hidup sehatmu", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HijauNyam) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HijauNyam) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { },
                    modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> { isPasswordVisible = true }
                            MotionEvent.ACTION_UP -> {
                                // Delay 2 detik sebelum sembunyi lagi
                                Handler(Looper.getMainLooper()).postDelayed({ isPasswordVisible = false }, 2000)
                            }
                        }
                        true
                    }
                ) {
                    Icon(if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Login Email/Password
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    scope.launch {
                        val userData = authManager.loginWithEmail(email, password)
                        if (userData != null) {
                            onAuthSuccess(userData)
                        } else {
                            Toast.makeText(context, "Email atau Password Salah", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Isi email dan password!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HijauNyam),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(" OR ", color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TOMBOL GOOGLE SIGN IN (Fix Poin #2: Force Sign Out agar bisa ganti akun)
        OutlinedButton(
            onClick = {
                authManager.signOutGoogle {
                    googleSignInLauncher.launch(authManager.googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Unspecified)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Link ke Register (Poin #4)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun? ", color = Color.Gray)
            TextButton(
                onClick = { onNavigateToRegister() },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Daftar Disini",
                    color = HijauNyam,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}