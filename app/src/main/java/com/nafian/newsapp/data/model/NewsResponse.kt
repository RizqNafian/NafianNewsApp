package com.nafian.newsapp.data.model

/**
 * data class NewsResponse mempreprentasikan entity dari NewsResponse.
 */
data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)