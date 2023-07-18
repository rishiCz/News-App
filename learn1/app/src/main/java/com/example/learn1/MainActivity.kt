package com.example.learn1
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn1.DataClass.DataButtons
import com.example.learn1.DataClass.DataNews
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), buttonClickListener,newsClickListener {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var buttonAdapter: ButtonAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsList : List<DataNews>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val splashlayouut = findViewById<LinearLayout>(R.id.splashLayoout)
        val savedNewsButton = findViewById<ImageButton>(R.id.savedNewsButton)
        val searchNews = findViewById<SearchView>(R.id.editText_enter_name)
        val topHeadlinesButton = findViewById<Button>(R.id.getTopHeadlines)
        val buisnessNewsButton = findViewById<Button>(R.id.getBuisnessNews)
        val recyclerV = findViewById<RecyclerView>(R.id.buttonRecyclerV)
        val newsRecyclerV = findViewById<RecyclerView>(R.id.newsRecyclerMain)
        val addNewsButton = findViewById<Button>(R.id.addNewsButton)
        val addNewsLayout = findViewById<LinearLayout>(R.id.addNewsLaout)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val newsNameEditText = findViewById<EditText>(R.id.newsName)
        val newsQueryEditText = findViewById<EditText>(R.id.newsQuery)

        recyclerV.isNestedScrollingEnabled = false
        val horizontalLayout = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        newsRecyclerV.layoutManager = horizontalLayout
        recyclerV.layoutManager = LinearLayoutManager(this)
        viewModel.getSavedButtons().observe(this) { buttonList ->
            buttonAdapter = ButtonAdapter(buttonList, this)
            recyclerV.adapter = buttonAdapter
        }

        lifecycleScope.launch{
            newsList = viewModel.fetchData()
            newsAdapter = NewsAdapter(newsList, R.layout.main_item_news, this@MainActivity)
            newsRecyclerV.adapter = newsAdapter
        }
        val intent = Intent(this,News::class.java)
        searchNews.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                intent.replaceExtras(null)
                val bundle = Bundle()
                bundle.putString("newsType", "everything")
                bundle.putString("query", "q=${text?.replace(' ','_')}")
                intent.putExtras(bundle)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        savedNewsButton.setOnClickListener{
            intent.replaceExtras(null)
            val bundle = Bundle()
            bundle.putString("saved", "true")
            intent.putExtras(bundle)
            startActivity(intent)
        }
        topHeadlinesButton.setOnClickListener{
            intent.replaceExtras(null)
            startActivity(intent)
        }

        buisnessNewsButton.setOnClickListener{
            intent.replaceExtras(null)
            val bundle = Bundle()
            bundle.putString("newsType", "everything")
            bundle.putString("query", "q=business")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        addNewsButton.setOnClickListener {
            addNewsButton.visibility = View.GONE
            addNewsLayout.visibility = View.VISIBLE
            lifecycleScope.launch {
                scrollDown()
            }
        }

        saveButton.setOnClickListener {
            val name = newsNameEditText.text.toString()
            val query = newsQueryEditText.text.toString().replace(' ','_')
            addItem(name,query)
            addNewsLayout.visibility = View.GONE
            addNewsButton.visibility = View.VISIBLE
            newsNameEditText.setText("")
            newsQueryEditText.setText("")
        }

        cancelButton.setOnClickListener {
            addNewsLayout.visibility = View.GONE
            addNewsButton.visibility = View.VISIBLE
        }

        Handler(Looper.getMainLooper()!!).postDelayed({
            splashlayouut.animate().apply {
                duration = 500
                translationY(2000f)
            }.withEndAction{splashlayouut.visibility = View.GONE}

        },2000)
    }

    private fun addItem(name:String, query:String){
        val button = DataButtons(name, query, 0)
        viewModel.insertSavedButton(button)
    }

    private suspend fun scrollDown(){
        delay(20)
        val scrollView = findViewById<ScrollView>(R.id.mainScrollView)
        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
    }

    override fun onClick(dataButtons: DataButtons) {
        val intent = Intent(this,News::class.java)
        intent.replaceExtras(null)
        val bundle = Bundle()
        bundle.putString("newsType", "everything")
        bundle.putString("query", "q=${dataButtons.query}")
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun deleteButton(dataButtons: DataButtons) {
        Toast.makeText(this, "Removed Button", Toast.LENGTH_SHORT).show()
        viewModel.deleteSavedButton(dataButtons)
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
       return
    }
}