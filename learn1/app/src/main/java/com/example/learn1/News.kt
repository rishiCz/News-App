package com.example.learn1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class News : AppCompatActivity(),newsClickListener {

    private lateinit var mAdapter: NewsAdapter
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

        val loadingbar = findViewById<ProgressBar>(R.id.progressBar2)
        val backButton = findViewById<ImageButton>(R.id.newsBackButton)
        backButton.setOnClickListener{
            this.onBackPressed()
        }
        val recyclerV = findViewById<RecyclerView>(R.id.recyclerV)
        recyclerV.layoutManager = LinearLayoutManager(this)
        val bundle = intent.extras

        if(bundle!=null){
            val newsType = bundle.getString("newsType").toString()
            val query = bundle.getString("query").toString()
            fetchData(recyclerV,newsType,query)
        }
        else fetchData(recyclerV)
    }

    public fun  fetchData(recyclerView: RecyclerView, newsType: String = "top-headlines", query: String = "country=in",view:Int = R.layout.items_news, listener:newsClickListener=this@News){
        val key= "&apiKey=e4d8cf10d30147dd9f8c8a39981315e1"
        Log.d("newsurl", "$newsType    $query")
        item.clear()
        CoroutineScope(Dispatchers.Main).launch {
            try {
               val response = newsApi.getDynamicNews("/v2/$newsType?$query$key")
                item = response.body()!!.articles.toMutableList()
                mAdapter = NewsAdapter(item,view,listener)
                recyclerView.adapter = mAdapter
                Log.d("APIRES", item.toString())

            } catch (e: Exception) {
                Log.d("NewsError", e.toString())
            }
        }
    }

    override fun onNewsClick(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onShareClick(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,url)
        val chooser = Intent.createChooser(intent,"Share this news using:")
        startActivity(chooser)
    }

}