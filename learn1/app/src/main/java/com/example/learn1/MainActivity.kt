package com.example.learn1
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val genButton = findViewById<Button>(R.id.cardButton)
        val memeButton = findViewById<Button>(R.id.getMeme)
        val newsButton = findViewById<Button>(R.id.getNews)
        val textView = findViewById<EditText>(R.id.editText_enter_name)
        genButton.setOnClickListener {
            val data = textView.editableText.toString()
            val intent = Intent(this,cardActivity::class.java)
            intent.putExtra("name",data)
            startActivity(intent)
        }
        memeButton.setOnClickListener {
            val intent = Intent(this,Meme::class.java)
            startActivity(intent)
        }
        newsButton.setOnClickListener{
            val intent = Intent(this,News::class.java)
            startActivity(intent)
        }

    }

}