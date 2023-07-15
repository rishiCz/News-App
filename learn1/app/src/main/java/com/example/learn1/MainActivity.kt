package com.example.learn1
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log.d
import android.view.View
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn1.Dao.ButtonDao
import com.example.learn1.Dao.NewsDao
import com.example.learn1.dataBase.MyDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), buttonClickListener,newsClickListener {
    private lateinit var mAdapter: ButtonAdapter
    var queryButtonList = mutableListOf<DataButtons>()
    val newsObj = News()
    lateinit var buttonDao:ButtonDao
    lateinit var newsDao: NewsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val Splashlayouut = findViewById<LinearLayout>(R.id.splashLayoout)
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

        buttonDao = MyDatabase.getButtonDatabase(application)!!.buttonDao
        newsDao = MyDatabase.getNewsDatabase(application)!!.newsDao

        recyclerV.setNestedScrollingEnabled(false);
        val horizontalLayout = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        newsRecyclerV.layoutManager = horizontalLayout
        recyclerV.layoutManager = LinearLayoutManager(this)

        newsObj.fetchData(newsRecyclerV, view = R.layout.main_item_news, listener = this@MainActivity)
        mAdapter = ButtonAdapter(queryButtonList,this)
        recyclerV.adapter = mAdapter
        val intent = Intent(this,News::class.java)
        searchNews.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                intent.replaceExtras(null)
                val bundle = Bundle()
                bundle.putString("newsType", "everything")
                bundle.putString("query", "q=${text?.replace(' ','_')}")
                intent.putExtras(bundle)
                startActivity(intent)
                return true;
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
            Splashlayouut.animate().apply {
                duration = 500
                translationY(2000f)
            }.withEndAction{Splashlayouut.visibility = View.GONE}

        },2000)
        queryButtonList.addAll(buttonDao.getButtonList())


    }

    private fun addItem(name:String, query:String){
        val id = genID()
        val obj = DataButtons(name, query, id)
        queryButtonList.add(obj)
        buttonDao.insertButton(obj)
        mAdapter.notifyItemInserted(queryButtonList.size)
    }

    private fun genID(): Int {
        if(queryButtonList.isEmpty())
            return 0
        return queryButtonList.last().id +1
    }

    suspend fun scrollDown(){
        delay(20)
        val scrollView = findViewById<ScrollView>(R.id.mainScrollView)
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
        queryButtonList.remove(dataButtons)
        d("deleteButton: ","${dataButtons.toString()}")
        buttonDao.deleteButton(dataButtons)
        mAdapter.notifyDataSetChanged()
    }

    override fun onNewsClick(news: DataNews) {
        val intent = Intent(this,NewsContent::class.java)
        intent.putExtra("newsObj",news)
        startActivity(intent)
//        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        startActivity(browserIntent)
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