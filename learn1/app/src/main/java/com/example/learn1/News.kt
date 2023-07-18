package com.example.learn1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn1.DataClass.DataNews
import kotlinx.coroutines.launch

class News : AppCompatActivity(), newsClickListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    lateinit var savedNewsList: List<DataNews>
    private var isSaved = false
    private lateinit var loadingbar: ProgressBar
    private lateinit var loadingTV: TextView
    private var item = listOf<DataNews>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        loadingbar = findViewById(R.id.progressBar2)
        loadingTV = findViewById(R.id.loadingText)
        val backButton = findViewById<ImageButton>(R.id.newsBackButton)
        val filterSearchView = findViewById<SearchView>(R.id.filterSearch)
        viewModel.getSavedNews().observeForever { savedNews ->
            savedNewsList = savedNews
        }
        val recyclerV = findViewById<RecyclerView>(R.id.recyclerV)
        recyclerV.layoutManager = LinearLayoutManager(this)

        filterSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null) {
                    if (intent.extras?.getString("saved") == "true" && this@News::savedNewsList.isInitialized)
                        filterList(savedNewsList, text, recyclerV)
                    else
                        filterList(item, text, recyclerV)
                }
                return true
            }
        })
        backButton.setOnClickListener {
            this.onBackPressed()
        }
        val bundle = intent.extras


        if (bundle != null) {
            if (bundle.getString("saved") == "true")
                loadSavedNews(recyclerV)
            else {
                val newsType = bundle.getString("newsType").toString()
                val query = bundle.getString("query").toString()
                loadIntoRecycler(recyclerV,newsType,query)
            }
        } else loadIntoRecycler(recyclerV)
    }

    override fun onResume() {
        super.onResume()
        if (this::newsAdapter.isInitialized && !isSaved) {
            genID()
            newsAdapter.notifyDataSetChanged()
        }
    }


    private fun filterList(NewsList: List<DataNews>, query: String, recyclerV:RecyclerView) {
        val filteredList = NewsList.filter { it.title.contains(query, ignoreCase = true) }
        newsAdapter = NewsAdapter(filteredList, R.layout.items_news, this)
        recyclerV.adapter = newsAdapter
    }

    private fun loadSavedNews(recyclerV: RecyclerView) {
        isSaved = true
        loadingTV.text = ""
        loadingbar.visibility = View.GONE
        viewModel.getSavedNews().observe(this@News) { savedNews ->
            newsAdapter = NewsAdapter(savedNews, R.layout.items_news, this@News)
            recyclerV.adapter = newsAdapter
            if (savedNews.isEmpty())
                loadingTV.text = "No Bookmarked News"
            if(this::newsAdapter.isInitialized)
                newsAdapter.notifyDataSetChanged()
        }
    }

    private fun loadIntoRecycler(recyclerV: RecyclerView,newsType: String = "top-headlines", query: String = "country=in"){
        lifecycleScope.launch{
            item = viewModel.fetchData(newsType,query)
            genID()
            newsAdapter = NewsAdapter(item, R.layout.items_news, this@News)
            recyclerV.adapter = newsAdapter
        }
    }

    private fun genID() {
        item.forEach { news ->
            for (item in savedNewsList) {
                news.id = item.id
                news.saved = true
                if (news == item)
                    break
                else{
                    news.saved = false
                    news.id = 0
                }
            }
        }
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
            news.id=viewModel.insertSaveNews(news)
        } else {
            viewModel.deleteSavedNews(news)
        }
        newsAdapter.notifyDataSetChanged()
    }

}