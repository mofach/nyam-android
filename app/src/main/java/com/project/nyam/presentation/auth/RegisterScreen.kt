package com.project.nyam.presentation.auth

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    // Parameter disesuaikan agar NyamApp bisa menangkap data user jika lewat Google
    onRegisterSuccess: (com.project.nyam.data.model.UserData?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val authManager = remember { AuthManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val HijauNyam = Color(0xFF4CAF50)
    val BackgroundSoft = Color(0xFFF8F9FA)

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
                        onRegisterSuccess(userData)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Sign-In Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    fun isPasswordValid(pass: String): Boolean {
        val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$".toRegex()
        return pattern.matches(pass)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoft)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        FilledIconButton(
            onClick = onBack,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = HijauNyam)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Daftar Akun", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2D3436))
        Text(text = "Mulai perjalanan sehatmu bersama NYAM", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = HijauNyam) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = HijauNyam) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.pointerInteropFilter {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> { isPasswordVisible = true }
                                    MotionEvent.ACTION_UP -> {
                                        Handler(Looper.getMainLooper()).postDelayed({ isPasswordVisible = false }, 2000)
                                    }
                                }
                                true
                            }
                        ) {
                            Icon(imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!isPasswordValid(password)) {
                            Toast.makeText(context, "Password wajib: 8+ karakter, Huruf Besar, Kecil, Angka, & Simbol!", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        scope.launch {
                            try {
                                auth.createUserWithEmailAndPassword(email, password).await()
                                Toast.makeText(context, "Berhasil! Silakan Login", Toast.LENGTH_SHORT).show()
                                onRegisterSuccess(null)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HijauNyam),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Daftar Sekarang", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("  OR  ", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                authManager.signOutGoogle {
                    googleSignInLauncher.launch(authManager.googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.Unspecified)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sign in with Google", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}