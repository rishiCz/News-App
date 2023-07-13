package com.example.learn1.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.learn1.DataButtons
import com.example.learn1.DataNews

@Dao
interface NewsDao {
    @Insert
    fun insertNews(news: DataNews)

    @Delete
    fun deleteNews(news: DataNews)

    @Query("SELECT * FROM DataNews")
    fun getSavedNewsList(): List<DataNews>

    @Update
    fun updateNews(news: DataNews)


}