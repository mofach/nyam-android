package com.project.nyam.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // ⚠️ PENTING: GANTI DENGAN URL ASLI CLOUD RUN KAMU
    // Jangan lupa akhiri dengan garis miring "/"
    private const val BASE_URL = "https://nyam-backend-956299547913.asia-southeast2.run.app/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Biar bisa lihat isi JSON di Logcat
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}