package com.example.learn1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class News : AppCompatActivity() {

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
        val recyclerV = findViewById<RecyclerView>(R.id.recyclerV)
        recyclerV.layoutManager = LinearLayoutManager(this)
        val bundle = intent.extras

        if(bundle!=null){
            val newsType = bundle.getString("newsType").toString()
            val query = bundle.getString("query").toString()
            fetchData(recyclerV,newsType,query)

        }
        else fetchData(recyclerV)
        Log.d("newsurl", bundle.toString())
    }

    private fun  fetchData(recyclerView: RecyclerView, newsType: String = "top-headlines", query: String = "country=in"){
        val key= "&apiKey=e4d8cf10d30147dd9f8c8a39981315e1"
        Log.d("newsurl", "$newsType    $query")
        CoroutineScope(Dispatchers.Main).launch {
            try {
               val response = newsApi.getDynamicNews("/v2/$newsType?$query$key")
                item = response.body()!!.articles.toMutableList()
                mAdapter = NewsAdapter(item)
                recyclerView.adapter = mAdapter
                Log.d("APIRES", item.toString())

            } catch (e: Exception) {
                Log.d("NewsError", e.toString())
            }
        }
    }
    fun open(){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        startActivity(browserIntent)
    }

}