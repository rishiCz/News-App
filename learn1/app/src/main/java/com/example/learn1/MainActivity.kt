package com.example.learn1
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), buttonClickListener {
    private lateinit var mAdapter: ButtonAdapter
    var item = mutableListOf<DataButtons>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layouut = findViewById<ConstraintLayout>(R.id.mainConstraintLayout)
        val searchNews = findViewById<SearchView>(R.id.editText_enter_name)
        val topHeadlinesButton = findViewById<Button>(R.id.getTopHeadlines)
        val buisnessNewsButton = findViewById<Button>(R.id.getBuisnessNews)
        val recyclerV = findViewById<RecyclerView>(R.id.buttonRecyclerV)
        val addNewsButton = findViewById<Button>(R.id.addNewsButton)
        val addNewsLayout = findViewById<LinearLayout>(R.id.addNewsLaout)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val newsNameEditText = findViewById<EditText>(R.id.newsName)
        val newsQueryEditText = findViewById<EditText>(R.id.newsQuery)
        recyclerV.setNestedScrollingEnabled(false);

        recyclerV.layoutManager = LinearLayoutManager(this)
        mAdapter = ButtonAdapter(item,this)
        recyclerV.adapter = mAdapter
        val intent = Intent(this,News::class.java)

        searchNews.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                val bundle = Bundle()
                bundle.putString("newsType", "everything")
                bundle.putString("query", "q=$text")
                intent.putExtras(bundle)
                startActivity(intent)
                return true;
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

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
        }

        saveButton.setOnClickListener {
            val name = newsNameEditText.text.toString()
            val query = newsQueryEditText.text.toString()
            addItem(name,query)
            addNewsLayout.visibility = View.GONE
            addNewsButton.visibility = View.VISIBLE
        }

        cancelButton.setOnClickListener {
            addNewsLayout.visibility = View.GONE
            addNewsButton.visibility = View.VISIBLE
        }


    }

    private fun addItem(name:String, query:String){
            val obj = DataButtons(name, query)
            item.add(obj)
            mAdapter.notifyItemInserted(item.size)
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

}