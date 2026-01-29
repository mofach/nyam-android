plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.project.nyam" // Pastikan ini sesuai package kamu
    compileSdk = 35 // Kita kunci di 35 yang stabil

    defaultConfig {
        applicationId = "com.project.nyam"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- INI RACIKAN STABIL (JANGAN DIUBAH DULU) ---

    // Core Android (Versi Aman)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Bill of Materials (BOM) - Penjaga Kestabilan Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))

    // UI & Material Design 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")

    // Navigasi (Versi Stabil)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Extended Icons (Buat ikon-ikon tambahan)
    implementation("androidx.compose.material:material-icons-extended")

    // Debugging Tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- FIREBASE (LOGIN & GOOGLE) ---
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // --- NETWORKING (RETROFIT) ---
    // Alat buat request ke Server
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Alat buat baca JSON dari Server
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Alat logging (biar kelihatan kalau error di Logcat)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")
}