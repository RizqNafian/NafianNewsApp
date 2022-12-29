package com.nafian.newsapp.ui.savednews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    // membuat variable untuk pengambilan data article
    private val savedArticleEventChannel = Channel<SavedArticleEvent>()
    val savedArticleEvent = savedArticleEventChannel.receiveAsFlow()

    // fungsi untuk mengambil semua article yang tersimpan
    fun getAllArticles() = newsRepository.getAllArticles()

    // peringatan pengambalin artikel yang akan dihapus sebelum terhapus
    fun onArticleSwiped(article: Article) {
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
            savedArticleEventChannel.send(SavedArticleEvent.ShowUndoDeleteArticleMessage(article))
        }
    }

    // fungsi untuk menghapus artikel
    fun onUndoDeleteClick(article: Article) {
        viewModelScope.launch {
            newsRepository.insertArticle(article)
        }
    }
    // menampilkan pengembalian saat article di hapus
    sealed class SavedArticleEvent{
        data class ShowUndoDeleteArticleMessage(val article: Article): SavedArticleEvent()
    }
}