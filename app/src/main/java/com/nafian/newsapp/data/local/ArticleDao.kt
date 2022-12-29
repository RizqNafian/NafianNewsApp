package com.nafian.newsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nafian.newsapp.data.model.Article

/**
 * method utnuk mengakses fungsi database.
 */
@Dao
interface ArticleDao {

    //memanggil data yang ada pada article_table
    @Query("SELECT * FROM article_table")
    fun getArticles() : LiveData<List<Article>>

    //memasukan data ke article_table dari artikel yang disimpan
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article) : Long

    //menghapus data dari article_table setalah artikel yang disimpan diahpus
    @Delete
    suspend fun delete(article: Article)

    //menghapus semua data dari article_table
    @Query("DELETE FROM article_table")
    suspend fun deleteAllArticles()
}