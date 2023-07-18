package com.example.learn1

import android.content.ContentValues.TAG
import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn1.Dao.NewsDao
import com.example.learn1.DataClass.DataNews
import com.example.learn1.dataBase.NewsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class News : AppCompatActivity(), newsClickListener {


    lateinit var mAdapter: NewsAdapter
    lateinit var newsDao: NewsDao
    lateinit var savedNewsList: List<DataNews>
    var isSaved = false
    lateinit var recyclerV: RecyclerView
    lateinit var loadingbar: ProgressBar
    lateinit var loadingTV: TextView
    var item = mutableListOf<DataNews>()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl("https://newsapi.org/")
        .build()
    private val newsApi = retrofit.create(ApiCalls::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        loadingbar = findViewById(R.id.progressBar2)
        loadingTV = findViewById(R.id.loadingText)
        val backButton = findViewById<ImageButton>(R.id.newsBackButton)
        val filterSearchView = findViewById<SearchView>(R.id.filterSearch)
        newsDao = NewsDatabase.getNewsDatabase(application)!!.newsDao
        newsDao.getSavedNewsList().observeForever { savedNews ->
            savedNewsList = savedNews
        }

        filterSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null) {
                    if (intent.extras?.getString("saved") == "true" && this@News::savedNewsList.isInitialized)
                        filterList(savedNewsList as MutableList<DataNews>, text)
                    else
                        filterList(item, text)
                }
                return true
            }

        })
        backButton.setOnClickListener {
            this.onBackPressed()
        }
        recyclerV = findViewById(R.id.recyclerV)
        recyclerV.layoutManager = LinearLayoutManager(this)
        val bundle = intent.extras


        if (bundle != null) {
            if (bundle.getString("saved") == "true")
                loadSavedNews()
            else {
                val newsType = bundle.getString("newsType").toString()
                val query = bundle.getString("query").toString()
                loadIntoRecycler(newsType,query)
            }
        } else loadIntoRecycler()
    }

    override fun onResume() {
        super.onResume()
        if (this::mAdapter.isInitialized && !isSaved) {
            checkNews(item)
            mAdapter!!.notifyDataSetChanged()
        }
    }


    private fun filterList(NewsList: MutableList<DataNews>, query: String) {
        val filteredList = NewsList.filter { it.title.contains(query, ignoreCase = true) }
        mAdapter = NewsAdapter(filteredList, R.layout.items_news, this)
        recyclerV.adapter = mAdapter
    }

    private fun loadSavedNews() {
        isSaved = true
        loadingTV.text = ""
        loadingbar.visibility = View.GONE
        newsDao.getSavedNewsList().observe(this@News) { savedNews ->
            mAdapter = NewsAdapter(savedNews, R.layout.items_news, this@News)
            recyclerV.adapter = mAdapter
            if (savedNews.isEmpty())
                loadingTV.text = "No Bookmarked News"
            if(this::mAdapter.isInitialized)
                mAdapter.notifyDataSetChanged()
        }
    }

    fun loadIntoRecycler(newsType: String = "top-headlines",query: String = "country=in"){
        lifecycleScope.launch{
            item = fetchData(newsType,query)
            mAdapter = NewsAdapter(item, R.layout.items_news, this@News)
            recyclerV.adapter = mAdapter
        }
    }

    suspend fun fetchData(newsType: String = "top-headlines",query: String = "country=in",): MutableList<DataNews> {
        val key = "&apiKey=e4d8cf10d30147dd9f8c8a39981315e1"
        var newsList= mutableListOf<DataNews>()
        try {
            val response = newsApi.getDynamicNews("/v2/$newsType?$query$key")
            newsList = response.body()?.articles as MutableList<DataNews>
            checkNews(newsList);
            Log.d("APIRES", item.toString())

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
            genID(news)
        }
    }

    private fun genID(news: DataNews) {
        Log.d(TAG, "ran1")
        news.saved = true
        for (item in savedNewsList) {
            news.id = item.id
            if (news == item)
                return
        }
        news.saved = false
        news.id = 0
    }


    override fun onNewsClick(news: DataNews) {
        val intent = Intent(this, NewsContent::class.java)
        intent.putExtra("newsObj", news)
        startActivity(intent)
    }

    override fun onShareClick(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        val chooser = Intent.createChooser(intent, "Share this news using:")
        startActivity(chooser)
    }

    override fun onSaveClick(news: DataNews) {
        if (news.saved) {
            news.id=newsDao.insertNews(news)
        } else {
            newsDao.deleteNews(news)
        }
        mAdapter!!.notifyDataSetChanged()
    }

}