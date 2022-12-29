package com.nafian.newsapp.ui.searchnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafian.newsapp.R
import com.nafian.newsapp.adapter.ArticlesAdapter
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.databinding.FragmentSearchNewsBinding
import com.nafian.newsapp.util.QUERY_PAGE_SIZE
import com.nafian.newsapp.util.Resource
import com.nafian.newsapp.util.SEARCH_NEWS_TIME_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SearchNewsFragment untuk mengatur tata letak dari layout dan membuat event/proses tertentu
 */
private const val TAG = "SearchNewsFragment"

@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news),
    ArticlesAdapter.OnItemClickListener {

    // memanggil SearchNewsViewModel
    private val viewModel: SearchNewsViewModel by viewModels()
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    // mengatur tampilan pada layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchNewsBinding.bind(view)
        val articleAdapter = ArticlesAdapter(this)

        binding.apply {
            rvSearchNews.apply {
                adapter = articleAdapter
                setHasFixedSize(true)
                addOnScrollListener(this@SearchNewsFragment.scrollListener)
            }
        }

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        // event saat pencarian terjadi
        viewModel.searchNews.observe(viewLifecycleOwner) {
            when (it) {
                // jika data ditemuka
                is Resource.Success -> {
                    isLoading = false
                    paginationProgressBar.visibility = View.INVISIBLE
                    it.data?.let { newsResponse ->
                        articleAdapter.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage)
                            rvBreakingNews.setPadding(0,0,0,0)
                    }
                }
                // jika data tidak ditemukan
                is Resource.Error -> {
                    isLoading = true
                    paginationProgressBar.visibility = View.INVISIBLE
                    it.message?.let { message ->
                        Log.e(TAG, "Error: $message")
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    // fungsi untuk scroll
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){ //State is scrolling
                isScrolling = true
            }
        }
        // event saat scoroll terjadi
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    // fungsi untuk klik (berpindah ke preview article)
    override fun onItemClick(article: Article) {
        val action = SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}