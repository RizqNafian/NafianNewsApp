package com.nafian.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nafian.newsapp.data.model.Article
import com.nafian.newsapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

/**
 * method utnuk membuat database.
 */
@Database(entities = [Article::class], version = 2)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    class Callback @Inject constructor(
        private val database: Provider<ArticleDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}