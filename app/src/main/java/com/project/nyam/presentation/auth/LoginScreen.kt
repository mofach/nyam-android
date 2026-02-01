package com.project.nyam.presentation.auth

import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.project.nyam.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onAuthSuccess: (com.project.nyam.data.model.UserData) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { AuthManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val HijauNyam = Color(0xFF4CAF50)
    val PutihBersih = Color(0xFFFFFFFF)

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
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Login Google Dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PutihBersih)
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // --- HEADER: LOGO & WELCOME ---
        Image(
            painter = painterResource(id = R.drawable.nyam_logo),
            contentDescription = "NYAM Logo",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome Back!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = "Not Your Average Menu",
            fontSize = 12.sp,
            color = HijauNyam,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- INPUT FIELDS ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, null, tint = HijauNyam) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HijauNyam,
                focusedLabelColor = HijauNyam
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = HijauNyam) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { },
                    modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> isPasswordVisible = true
                            MotionEvent.ACTION_UP -> isPasswordVisible = false
                        }
                        true
                    }
                ) {
                    Icon(if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HijauNyam,
                focusedLabelColor = HijauNyam
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- LOGIN BUTTONS ---
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    scope.launch {
                        val userData = authManager.loginWithEmail(email, password)
                        if (userData != null) onAuthSuccess(userData)
                        else Toast.makeText(context, "Email atau Password Salah", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HijauNyam),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
            Text("  OR  ", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                authManager.signOutGoogle {
                    googleSignInLauncher.launch(authManager.googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo), // Pastikan ada logo google di drawable
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sign in with Google", color = Color.Black, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FOOTER: REGISTER LINK ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onNavigateToRegister) {
                Text("Daftar Disini", color = HijauNyam, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- BRANDING FOOTER ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Built by the NYAM Team C242-PS136",
                fontSize = 10.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Refined by Aqil Muhammad Fachrezi",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}