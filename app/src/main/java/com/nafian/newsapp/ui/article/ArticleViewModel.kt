package com.nafian.newsapp.ui.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ArticleViewModel mengghubungkan data ke tampilan
 */
@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val articleEventChannel = Channel<ArticleEvent>()
    val articleEvent = articleEventChannel.receiveAsFlow()

    // melakukan proses penyimpanan dan jika penyimpanan berhasil akan muncul pesan "Article Saved."
    fun saveArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.insertArticle(article)
            articleEventChannel.send(ArticleEvent.ShowArticleSavedMessage("Article Saved."))
        }
    }

    sealed class ArticleEvent{
        data class ShowArticleSavedMessage(val message: String): ArticleEvent()
    }
}