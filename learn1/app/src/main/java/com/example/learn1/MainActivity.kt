package com.example.learn1
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layouut = findViewById<ConstraintLayout>(R.id.mainConstraintLayout)
        val searchNews = findViewById<SearchView>(R.id.editText_enter_name)
        val topHeadlinesButton = findViewById<Button>(R.id.getTopHeadlines)
        val buisnessNewsButton = findViewById<Button>(R.id.getBuisnessNews)
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
            startActivity(intent)
        }

        buisnessNewsButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("newsType", "everything")
            bundle.putString("query", "q=business")
            intent.putExtras(bundle)
            startActivity(intent)
        }

//        val myButton = Button(this)
//        myButton.layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        myButton.text="dynamic"
//        layouut.addView(myButton)


    }

}