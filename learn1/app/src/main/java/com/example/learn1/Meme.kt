package com.example.learn1

import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class Meme : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)
        val nextBtn = findViewById<Button>(R.id.nextBtn)
        val shareBtn = findViewById<Button>(R.id.shareBtn)
        val memeImage=findViewById<ImageView>(R.id.memeImage)
        val progressB=findViewById<ProgressBar>(R.id.progressBar)

        loadMeme(progressB,memeImage)
        shareBtn.setOnClickListener {
            val icon = BitmapFactory.decodeResource(this.resources,
                R.drawable.ic_launcher_background
            )
            val f = File.createTempFile("hey","jpg")
            val bos = ByteArrayOutputStream()
            icon.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata: ByteArray = bos.toByteArray()
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            val uri = Uri.fromFile(f)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM,uri)
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
        nextBtn.setOnClickListener {
            loadMeme(progressB,memeImage)
        }
    }

    private fun loadMeme (progressB:ProgressBar, memeImage:ImageView) {
        progressB.visibility = View.VISIBLE
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("https://meme-api.com/")
            .build()
        val a = retrofit.create(ApiCalls::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val r = a.getResponse()
                val memeUrl = r.body()?.url.toString()
                withContext(Dispatchers.Main) {
                    Glide.with(this@Meme).load(memeUrl).into(memeImage)
                    progressB.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.d("errorApi", e.toString())
            }
        }
    }

}