package com.example.learn1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn1.Dao.NewsDao
import com.example.learn1.dataBase.MyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class News : AppCompatActivity(),newsClickListener {


    var mAdapter: NewsAdapter? = null
    var newsDao: NewsDao? = null
    var isSaved = false
    lateinit var recyclerV: RecyclerView
    lateinit var loadingbar: ProgressBar
    lateinit var loadingTV:TextView
    var savedNewsList = mutableListOf<DataNews>()
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
        newsDao = MyDatabase.getNewsDatabase(application)!!.newsDao

        filterSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null) {
                    if (intent.extras?.getString("saved")=="true")
                        filterList(savedNewsList,text)
                    else
                        filterList(item,text)
                }
                return true
            }

        })
        backButton.setOnClickListener{
            this.onBackPressed()
        }
        recyclerV = findViewById<RecyclerView>(R.id.recyclerV)
        recyclerV.layoutManager = LinearLayoutManager(this)
        val bundle = intent.extras

        savedNewsList = newsDao!!.getSavedNewsList() as MutableList<DataNews>


        if(bundle!=null){
            if (bundle.getString("saved")=="true")
                loadSavedNews()
            else{
                val newsType = bundle.getString("newsType").toString()
                val query = bundle.getString("query").toString()
                fetchData(recyclerV,newsType,query)
            }
        }
        else fetchData(recyclerV)
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter !=null && !isSaved) {
            savedNewsList = newsDao!!.getSavedNewsList() as MutableList<DataNews>
            checkNews()
            mAdapter!!.notifyDataSetChanged()
        }
    }


    private fun filterList(NewsList: MutableList<DataNews>,query: String) {
        val filteredList = NewsList.filter { it.title.contains(query, ignoreCase = true) }
        mAdapter = NewsAdapter(filteredList,R.layout.items_news,this)
        recyclerV.adapter=mAdapter
    }

    private fun loadSavedNews() {
        isSaved = true
        loadingTV.text=""
        loadingbar.visibility=View.GONE
        if (savedNewsList.isEmpty())
            loadingTV.text="No Bookmarked News"
        Log.d( "loadSavedNews: ","ran")
        mAdapter = NewsAdapter(savedNewsList,R.layout.items_news,this)
        recyclerV.adapter = mAdapter
    }

    public fun  fetchData(recyclerView: RecyclerView, newsType: String = "top-headlines", query: String = "country=in",view:Int = R.layout.items_news, listener:newsClickListener=this@News){
        val key= "&apiKey=e4d8cf10d30147dd9f8c8a39981315e1"
        Log.d("newsurl", "$newsType    $query")
        item.clear()
        CoroutineScope(Dispatchers.Main).launch {
            try {
               val response = newsApi.getDynamicNews("/v2/$newsType?$query$key")
                item = response.body()?.articles as MutableList<DataNews>
                checkNews()
                mAdapter = NewsAdapter(item,view,listener)
                recyclerView.adapter = mAdapter
                Log.d("APIRES", item.toString())

            } catch (e: Exception) {
                Log.d("NewsError", e.toString())
            }
        }
    }
    private fun checkNews() {
        item.forEach { news ->
            news.author = news.author ?: "N/A"
            news.urlToImage = news.urlToImage ?: "null"
            news.title = news.title ?: "N/A"
            news.url = news.url ?: "N/A"
            news.content = news.content ?: "No Content to Display"
            news.description = news.description ?: "N/A"
            news.publishedAt = news.publishedAt ?: "N/A"
            news.id= genID(news)
        }
    }

    private fun genID(news: DataNews): Int {
        news.saved=true
        for(item in savedNewsList){
            news.id=item.id
            if (news == item)
                return news.id
        }
        news.saved=false
        return 0
    }


    override fun onNewsClick(news: DataNews) {
        val intent = Intent(this,NewsContent::class.java)
        intent.putExtra("newsObj",news)
        startActivity(intent)
    }

    override fun onShareClick(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,url)
        val chooser = Intent.createChooser(intent,"Share this news using:")
        startActivity(chooser)
    }

    override fun onSaveClick(news: DataNews) {
        if(news.saved){
            newsDao!!.insertNews(news)
        }
        else{
            newsDao!!.deleteNews(news)
            savedNewsList.remove(news)
            if (savedNewsList.isEmpty())
                loadingTV.text="No Bookmarked News"
        }
        mAdapter!!.notifyDataSetChanged()
    }

}