package com.example.learn1.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.learn1.DataClass.DataNews

@Dao
interface NewsDao {
    @Insert
    fun insertNews(news: DataNews):Long

    @Delete
    fun deleteNews(news: DataNews)

    @Query("SELECT * FROM DataNews ORDER BY id DESC")
    fun getSavedNewsList(): LiveData<List<DataNews>>

    @Update
    fun updateNews(news: DataNews)


}