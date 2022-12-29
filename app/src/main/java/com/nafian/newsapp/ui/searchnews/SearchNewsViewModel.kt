package com.nafian.newsapp.ui.searchnews

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

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // membuat variable untuk pengambilan data article
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsResponse: NewsResponse? = null
    var searchNewsPage = 1

    // fungsi pencarian
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewCall(searchQuery, searchNewsPage)
    }

    // // fungsi untuk checking article
    private suspend fun safeSearchNewCall(searchQuery: String, searchNewsPage: Int){
        searchNews.postValue(Resource.Loading())
        try{
            // article akan dicari kemudian dipanggil jika memiliki internet
            if(hasInternetConnection(context)){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            // jika tidak akan muncul event "No Internet Connection"
            else
                searchNews.postValue(Resource.Error("No Internet Connection"))
        }
        catch (ex: Exception){
            when(ex){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // fungsi untuk membuka halaman baru saat scroll sampai bawah
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null)
                    searchNewsResponse = resultResponse
                else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}