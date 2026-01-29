package com.project.nyam.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // Import ini wajib untuk timeout

object ApiClient {
    private const val BASE_URL = "https://nyam-backend-956299547913.asia-southeast2.run.app/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        // --- TAMBAHKAN TIMEOUT DI SINI ---
        .connectTimeout(60, TimeUnit.SECONDS) // Waktu maksimal untuk koneksi awal
        .readTimeout(60, TimeUnit.SECONDS)    // Waktu maksimal menunggu respon data
        .writeTimeout(60, TimeUnit.SECONDS)   // Waktu maksimal mengirim data
        // ---------------------------------
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