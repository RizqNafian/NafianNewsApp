package com.nafian.newsapp.ui.savednews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nafian.newsapp.R
import com.nafian.newsapp.adapter.ArticlesAdapter
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.databinding.FragmentSavedNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * SavedNewsFragment untuk mengatur tata letak dari layout dan membuat event tertentu
 */
@AndroidEntryPoint
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news),
    ArticlesAdapter.OnItemClickListener {

    // memanggil SavedNewsViewModel
    private val viewModel: SavedNewsViewModel by viewModels()

    // mengatur tampilan pada layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSavedNewsBinding.bind(view)
        val articleAdapter = ArticlesAdapter(this)
        binding.apply {
            rvSavedNews.apply {
                adapter = articleAdapter
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val article = articleAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onArticleSwiped(article)
                }
            }).attachToRecyclerView(rvSavedNews)
        }

        // mengambil semua articele yang tersimpan
        viewModel.getAllArticles().observe(viewLifecycleOwner) {
            articleAdapter.submitList(it)
        }

        // event saat article dihapus
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.savedArticleEvent.collect { event ->
                when (event) {
                    is SavedNewsViewModel.SavedArticleEvent.ShowUndoDeleteArticleMessage -> {
                        Snackbar.make(requireView(), "Article Deleted!",Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.article)
                            }.show()
                    }
                }
            }
        }
    }

    // fungsi untuk klik (berpindah ke preview article)
    override fun onItemClick(article: Article) {
        val action = SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}