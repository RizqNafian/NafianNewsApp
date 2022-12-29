package com.nafian.newsapp.repository

import com.nafian.newsapp.data.local.ArticleDao
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.data.model.NewsResponse
import com.nafian.newsapp.data.remote.NewsApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository untuk mengambil article dan menyimpanya secara offline.
 */
@Singleton
class NewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val articleDao: ArticleDao
) {

    // mengambil article terkini
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return newsApi.getBreakingNews(countryCode,pageNumber)
    }

    // melakukan pencarian article
    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse>{
        return newsApi.searchForNews(searchQuery, pageNumber)
    }

    // mengambil semua article yang ada
    fun getAllArticles() = articleDao.getArticles()

    // memasukan article yang di simpan ke database
    suspend fun insertArticle(article: Article) = articleDao.insert(article)

    // menghapus article yang tersimpan pada database
    suspend fun deleteArticle(article: Article) = articleDao.delete(article)

    // menghpus semua article yang tersimpan pada database
    suspend fun deleteAllArticles() = articleDao.deleteAllArticles()
}