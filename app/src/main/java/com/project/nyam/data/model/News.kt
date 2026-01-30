package com.project.nyam.data.model

data class NewsResponse(
    val status: String,
    val message: String,
    val data: List<NewsItem>
)

data class NewsItem(
    val source: String,
    val title: String,
    val link: String,
    val pubDate: String,
    val snippet: String,
    val thumbnail: String
)