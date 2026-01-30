package com.project.nyam.data.model

// Model untuk Pencarian Teks
data class SearchResponse(
    val status: String,
    val message: String,
    val data: SearchData
)

data class SearchData(
    val recipes: List<Recipe>
)

// Model untuk Prediksi Gambar
data class PredictResponse(
    val status: String,
    val message: String,
    val data: PredictData?
)

data class PredictData(
    val recognition: RecognitionInfo?,
    val recommendations: SearchData?
)

data class RecognitionInfo(
    val predicted_class: String,
    val predicted_prob: Double
)