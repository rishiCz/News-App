package com.example.learn1

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.learn1.Dao.ButtonDao
import com.example.learn1.Dao.NewsDao
import com.example.learn1.DataClass.DataButtons
import com.example.learn1.DataClass.DataNews
import com.example.learn1.dataBase.MyDatabase
import com.example.learn1.dataBase.NewsDatabase
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var newsDao: NewsDao
    private var buttonDao: ButtonDao

    init {
        buttonDao = MyDatabase.getButtonDatabase(application)!!.buttonDao
        newsDao = NewsDatabase.getNewsDatabase(application)!!.newsDao
    }

    // News DataBase
    fun getSavedNews():LiveData<List<DataNews>>{
        return newsDao.getSavedNewsList()
    }
    fun insertSaveNews(news:DataNews): Long {
        return newsDao.insertNews(news)
    }
    fun deleteSavedNews(news:DataNews){
        newsDao.deleteNews(news)
    }

    // Button DataBase
    fun getSavedButtons():LiveData<MutableList<DataButtons>>{
        return buttonDao.getButtonList()
    }
    fun deleteSavedButton(button: DataButtons){
        buttonDao.deleteButton(button)
    }
    fun insertSavedButton(button: DataButtons) {
        buttonDao.insertButton(button)
    }

    // News Api
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl("https://newsapi.org/")
        .build()
    private val newsApi = retrofit.create(ApiCalls::class.java)

    suspend fun fetchData(newsType: String = "top-headlines",query: String = "country=in"): List<DataNews> {
        val key = "&apiKey=e4d8cf10d30147dd9f8c8a39981315e1"
        var newsList= listOf<DataNews>()
        try {
            val response = newsApi.getDynamicNews("/v2/$newsType?$query$key")
            newsList = response.body()?.articles as List<DataNews>
            checkNews(newsList)
        } catch (e: Exception) {
            Log.d("NewsError", e.toString())
        }
        return newsList
    }

    private fun checkNews(newsList: List<DataNews>) {
        newsList.forEach { news ->
            news.author = news.author ?: "N/A"
            news.urlToImage = news.urlToImage ?: "null"
            news.title = news.title ?: "N/A"
            news.url = news.url ?: "N/A"
            news.content = news.content ?: "No Content to Display"
            news.description = news.description ?: "N/A"
            news.publishedAt = news.publishedAt ?: "N/A"
            news.id = 0
        }
    }
}