package com.nafian.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nafian.newsapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news.*

/**
 * base dari semua tampilan
 */
@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    /**
     * onCreate dipanggil sebelum layar pertama ditampilkan ke user.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}