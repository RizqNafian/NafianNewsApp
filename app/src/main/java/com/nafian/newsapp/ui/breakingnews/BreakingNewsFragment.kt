package com.nafian.newsapp.ui.breakingnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafian.newsapp.R
import com.nafian.newsapp.adapter.ArticlesAdapter
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.databinding.FragmentBreakingNewsBinding
import com.nafian.newsapp.util.QUERY_PAGE_SIZE
import com.nafian.newsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_breaking_news.*

/**
 * BreakingNewsFragment untuk mengatur tata letak dari layout dan membuat event tertentu
 */
private const val TAG = "BreakingNewsFragment"
@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news), ArticlesAdapter.OnItemClickListener {

    // memanggil BreakingNewsViewModel
    private val viewModel: BreakingNewsViewModel by viewModels()
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    // mengatur tampilan pada layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBreakingNewsBinding.bind(view)
        val articleAdapter = ArticlesAdapter(this)

        binding.apply {
            rvBreakingNews.apply {
                adapter = articleAdapter
                setHasFixedSize(true)
                addOnScrollListener(this@BreakingNewsFragment.scrollListener)
            }
        }

        // menghubungkan fragment dengan viewModel untuk mwngamabil list article
        viewModel.breakingNews.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success -> {
                    paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = false
                    it.data?.let { newsResponse ->
                        articleAdapter.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage)
                            rvBreakingNews.setPadding(0,0,0,0)
                    }
                }
                is Resource.Error -> {
                    paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = true
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error: $message")
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
                viewModel.getBreakingNews("tr")
                isScrolling = false
            }
        }
    }

    // fungsi untuk klik (berpindah ke preview article)
    override fun onItemClick(article: Article) {
        val action = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}