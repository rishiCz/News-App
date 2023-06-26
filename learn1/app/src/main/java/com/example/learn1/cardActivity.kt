package com.example.learn1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class cardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        val text ="Happy Birthday \n"+intent.getStringExtra("name").toString().uppercase()
        val tevi= findViewById<TextView>(R.id.text1)
        tevi.setText(text)
    }
}