package com.nafian.newsapp.ui.breakingnews

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafian.newsapp.data.model.NewsResponse
import com.nafian.newsapp.repository.NewsRepository
import com.nafian.newsapp.util.NetworkUtil.Companion.hasInternetConnection
import com.nafian.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/**
 * BreakingNewsViewModel menghubungkan data, event, dan proses yang terjadi pada fragment dengan
 *
 */
@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // membuat variable untuk pengambilan data article
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    // fungsi akan berjalan pertamakali saat halaman terbuka
    init {
        getBreakingNews("id")
    }

    // fungsi untuk mengambil artikel berdasar negara
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    // fungsi untuk checking article
    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            // article akan diperbaharui/dipanggil jika memiliki internet
            if(hasInternetConnection(context)){
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            // jika tidak akan muncul event "No Internet Connection"
            else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (ex : Exception){
            when(ex){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // fungsi untuk membuka halaman baru saat scroll sampai bawah
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null)
                    breakingNewsResponse = resultResponse
                else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}