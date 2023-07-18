package com.example.learn1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.learn1.DataClass.DataNews
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NewsContent : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    lateinit var savedNewsList: List<DataNews>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_content)


        val news= intent.extras?.getSerializable("newsObj") as DataNews

        val newsImage = findViewById<ImageView>(R.id.news_picture_view)
        val titleView = findViewById<TextView>(R.id.titleView_news_content)
        val authorView = findViewById<TextView>(R.id.authorView_news_content)
        val descriptioView = findViewById<TextView>(R.id.descriptionView_news_content)
        val contentView = findViewById<TextView>(R.id.contentView_news_content)
        val bookBut = findViewById<ImageButton>(R.id.bookmarkButton)
        val dateTV = findViewById<TextView>(R.id.date_text_view)
        val shareButton = findViewById<ImageButton>(R.id.shareButton)
        val browserButton = findViewById<Button>(R.id.browserButton)
        val backButton = findViewById<ImageButton>(R.id.newsBackButton_news_content)
        lifecycleScope.launch {
            viewModel.getSavedNews().observeForever { savedNews ->
                savedNewsList = savedNews
            }
            while(!this@NewsContent::savedNewsList.isInitialized) {
                delay(1)
            }
            genID(news)
            updateButton(news.saved, bookBut)
        }
            titleView.text=news.title
            authorView.text="-${news.author}"
            descriptioView.text= news.description
            contentView.text= news.content.replace(Regex("\\[([^\\[\\]]+)]"),"")
            dateTV.text=news.publishedAt.substring(0..9)





        if(news.urlToImage == "null")
            newsImage.setImageResource(R.drawable.image_not_loaded)
        else
            Glide.with(this)
                .load(news.urlToImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                .into(newsImage)

        bookBut.setOnClickListener{onSaveClick(news,bookBut)}
        shareButton.setOnClickListener{onShareClick(news.url)}
        browserButton.setOnClickListener{onBrowserClick(news.url)}
        backButton.setOnClickListener{onBackPressed()}

    }

    private fun onBrowserClick(url: String) {
        val intent = CustomTabsIntent.Builder()
            .build()
        intent.launchUrl(this, Uri.parse(url))
    }

    private fun onShareClick(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,url)
        val chooser = Intent.createChooser(intent,"Share this news using:")
        startActivity(chooser)
    }

    private fun updateButton(saved:Boolean, bookbut:ImageButton){
        val background = if(saved) R.drawable.bookmark_saved else R.drawable.bookmark_not_saved
        bookbut.setBackgroundResource(background)
    }

    private fun onSaveClick(news: DataNews, bookBut: ImageButton) {
        news.saved = !news.saved
        updateButton(news.saved, bookBut)
        if(news.saved)  news.id=viewModel.insertSaveNews(news)
        else            viewModel.deleteSavedNews(news)
    }

    private fun genID(news: DataNews) {
        news.saved=true
        for(item in savedNewsList){
            news.id=item.id
            if (news == item)
                return
        }
        news.saved=false
        news.id=0
        return
    }
}